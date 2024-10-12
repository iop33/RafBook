package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import app.mutex.DistributedMutex;
import servent.handler.*;
import servent.handler.backup.*;
import servent.handler.files.*;
import servent.handler.organization.DoneReorganizingHandler;
import servent.handler.friends.AddFriendHandler;
import servent.handler.friends.NoPermissionHandler;
import servent.handler.suzuki.SuzukiGiveTokenHandler;
import servent.handler.suzuki.SuzukiRequestHandler;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class SimpleServentListener implements Runnable, Cancellable {

    private volatile boolean working = true;
    private DistributedMutex mutex;

    public SimpleServentListener(DistributedMutex mutex) {
        this.mutex = mutex;
    }

    /*
     * Thread pool for executing the handlers. Each client will get it's own handler thread.
     */
    private final ExecutorService threadPool = Executors.newWorkStealingPool();

    @Override
    public void run() {
        ServerSocket listenerSocket = null;
        try {
            listenerSocket = new ServerSocket(AppConfig.myServentInfo.getListenerPort(), 100);
            /*
             * If there is no connection after 1s, wake up and see if we should terminate.
             */
            listenerSocket.setSoTimeout(1000);
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("Couldn't open listener socket on: " + AppConfig.myServentInfo.getListenerPort());
            System.exit(0);
        }


        while (working) {
            try {
                Message clientMessage;

                Socket clientSocket = listenerSocket.accept();

                //GOT A MESSAGE! <3
                clientMessage = MessageUtil.readMessage(clientSocket);

                MessageHandler messageHandler = new NullHandler(clientMessage);

                /*
                 * Each message type has it's own handler.
                 * If we can get away with stateless handlers, we will,
                 * because that way is much simpler and less error prone.
                 */
                switch (clientMessage.getMessageType()) {
                    case NEW_NODE:
                        messageHandler = new NewNodeHandler(clientMessage, mutex);
                        break;
                    case REMOVE_NODE:
                        messageHandler = new RemoveNodeHandler(clientMessage);
                        break;
                    case WELCOME:
                        messageHandler = new WelcomeHandler(clientMessage, mutex);
                        break;
                    case SORRY:
                        messageHandler = new SorryHandler(clientMessage);
                        break;
                    case UPDATE:
                        messageHandler = new UpdateHandler(clientMessage);
                        break;
                    case ADD_FILE:
                        messageHandler = new AddFileHandler(clientMessage);
                        break;
                    case REMOVE_FILE:
                        messageHandler = new RemoveFileHandler(clientMessage);
                        break;
                    case FILE_REMOVED:
                        messageHandler = new FileRemovedHandler(clientMessage);
                        break;
                    case ASK_GET:
                        messageHandler = new AskGetHandler(clientMessage);
                        break;
                    case GET_PORT_FILES:
                        messageHandler = new GetFilesOnPortHandler(clientMessage);
                        break;
                    case TELL_GET:
                        messageHandler = new TellGetHandler(clientMessage);
                        break;
                    case TELL_PORT_FILES:
                        messageHandler = new TellPortFilesHandler(clientMessage);
                        break;
                    case POISON:
                        break;
                    case NO_PERMISSION:
                        messageHandler = new NoPermissionHandler(clientMessage);
                        break;
                    case ADD_FRIEND:
                        messageHandler = new AddFriendHandler(clientMessage);
                        break;
                    case PING:
                        messageHandler = new PingHandler(clientMessage);
                        break;
                    case PONG:
                        messageHandler = new PongHandler(clientMessage);
                        break;
                    case IS_ALIVE:
                        messageHandler = new IsAliveHandler(clientMessage);
                        break;
                    case FAILED_NODE:
                        messageHandler = new FailedNodeHandler(clientMessage);
                        break;
                    case FAILED_NODE_CONFIRM:
                        messageHandler = new FailedNodeConfirmHandler(clientMessage);
                        break;
                    case SUZUKI_REQUEST:
                        messageHandler = new SuzukiRequestHandler(clientMessage);
                        break;
                    case SEND_BACKUP:
                        messageHandler = new ReceiveBackupHandler(clientMessage);
                        break;
                    case SUZUKI_GIVE_TOKEN:
                        messageHandler = new SuzukiGiveTokenHandler(clientMessage);
                        break;
                    case DONE_REORGANIZING:
                        messageHandler = new DoneReorganizingHandler(clientMessage);
                        break;
                }


                threadPool.submit(messageHandler);
            } catch (SocketTimeoutException timeoutEx) {
                //Uncomment the next line to see that we are waking up every second.
//				AppConfig.timedStandardPrint("Waiting...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        this.working = false;
    }

}
