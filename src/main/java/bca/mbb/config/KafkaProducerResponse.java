package bca.mbb.config;

import org.apache.kafka.clients.producer.RecordMetadata;

public class KafkaProducerResponse {

    private boolean success;
    private Exception exception;
    private RecordMetadata recMetadata;

    public KafkaProducerResponse(boolean success, Exception exception, RecordMetadata recMetadata) {
        super();
        this.success = success;
        this.exception = exception;
        this.recMetadata = recMetadata;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public RecordMetadata getRecMetadata() {
        return recMetadata;
    }

    public void setRecMetadata(RecordMetadata recMetadata) {
        this.recMetadata = recMetadata;
    }
}
