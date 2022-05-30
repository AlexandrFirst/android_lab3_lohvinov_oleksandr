package oleksandr.lohvinov.lab3.other;

public class Resource<T> {

    private Status status;
    private T data;
    private String message;

    public Resource(Status status, T data, String message) {
        this.status = status;
        this.data = data;

        this.message = message;
    }

    public static<T> Resource success(T data){
        return new Resource(Status.SUCCESS, data, null);
    }

    public static<T> Resource error(String message, T data){
        return new Resource(Status.ERROR, data, message);
    }

    public static<T> Resource loading(T data){
        return new Resource(Status.LOADING, data, null);
    }

    public Status getStatus() {
        return status;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
