package sample;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.collections.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Controller {

    @FXML
    public void initialize() {
        Observable<String> o1 = JavaFxObservable.eventsOf(textField, KeyEvent.KEY_RELEASED)
                .map(keyEvent -> textField.getText())
                .debounce(3, TimeUnit.SECONDS);

        Observable<String> o2 = JavaFxObservable.actionEventsOf(comboBox)
                .doOnNext(x -> System.out.println(x))
                .filter(x -> x!=null)
                .map(actionEvent -> comboBox.getSelectionModel().getSelectedItem().toString());

        o1.subscribe(s -> System.out.println(s));
        o2.subscribe(s -> System.out.println(s));

        Observable<String> o3 = Observable.merge(o1,o2);

        o3.map(s -> search(s))
                .map(stringObservable -> stringObservable.toList().blockingGet())
                .observeOn(Schedulers.io())
                .subscribeOn(JavaFxScheduler.platform())
                .doOnError(throwable -> throwable.printStackTrace())
                .subscribe(strings -> {
                    Platform.runLater(() -> {
                       comboBox.setItems(FXCollections.observableList(strings));
                    });
                });
    }

    @FXML
    ComboBox comboBox;
    @FXML
    TextField textField;

    public Observable<String> search(String s) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Observable.just(s + 1, s + 2, s + 3);
    }
}
