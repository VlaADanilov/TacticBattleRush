package org.example.client.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.example.client.connectors.ClientImpl;
import org.example.client.protocol.Message;


public class MessageWaitingService extends Service<Boolean> {

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                Message message = ClientImpl.getInstance().getMessage();
                if(message.getType() == 1 && new String(message.getData()).equals("начать")){
                    updateValue(true);
                    return true;
                }
                return false;
            }
        };
    }


    public void cancelWaiting() {
        if (isRunning()) {
            this.cancel();
        }
    }
}