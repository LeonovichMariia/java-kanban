package kanbanCore.entity;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(int id, Status status, String description, String name, Epic epic) {
        super(id, status, description, name);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }


}
