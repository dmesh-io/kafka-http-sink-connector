package nz.ac.auckland.kafka.http.sink.handler;

import nz.ac.auckland.kafka.http.sink.HttpSinkConnectorConfig;
import org.apache.kafka.connect.sink.SinkTaskContext;

public class ResponseExceptionStrategyHandlerFactory {

    public enum ExceptionStrategy{
        PROGRESS_BACK_OFF_DROP_MESSAGE,
        PROGRESS_BACK_OFF_STOP_TASK,
        DROP_MESSAGE,
        STOP_TASK,
        DEAD_LETTER_QUEUE
    }

    public static ExceptionHandler getInstance(ExceptionStrategy exceptionStrategy,HttpSinkConnectorConfig config, SinkTaskContext context){
        switch(exceptionStrategy){
            case PROGRESS_BACK_OFF_DROP_MESSAGE: return new ProgressiveBackoffDropHandler(config,context);
            case PROGRESS_BACK_OFF_STOP_TASK: return new ProgressiveBackoffStopTaskHandler(config,context,config.responseRetryBackoffsec);
            case DROP_MESSAGE: return new DropMessageHandler(context);
            case STOP_TASK: return new StopTaskHandler();
            case DEAD_LETTER_QUEUE: return new DeadLetterQueueHandler(context.errantRecordReporter());
            default: throw new StrategyNotFoundException();
        }
    }


    private static class StrategyNotFoundException extends RuntimeException{
    }
}
