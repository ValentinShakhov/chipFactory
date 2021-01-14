import java.util.Objects;

class Machine {

    int id;
    int performance;

    public Machine(int index, int performance) {
        this.id = index;
        this.performance = performance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Machine machine = (Machine) o;
        return id == machine.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.valueOf(performance);
    }
}