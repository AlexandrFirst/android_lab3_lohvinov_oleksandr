package oleksandr.lohvinov.lab3.other;

public class Event<T> {
    private T data;
    private boolean hasBeenHandled = false;

    public Event(T data) {
        this.data = data;
    }

    public boolean isHasBeenHandled() {
        return hasBeenHandled;
    }

    private void setHasBeenHandled(boolean hasBeenHandled) {
        this.hasBeenHandled = hasBeenHandled;
    }

    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return data;
        }
    }

    public T peekContent(){
        return data;
    }
}
