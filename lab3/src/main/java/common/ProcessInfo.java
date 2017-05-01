package common;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by 1 on 01.05.2017.
 */
public class ProcessInfo implements Serializable {
    private UUID uuid = UUID.randomUUID();
    private Thread process;
    int load;

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Thread getProcess() {
        return process;
    }

    public void setProcess(Thread process) {
        this.process = process;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessInfo that = (ProcessInfo) o;

        return uuid != null ? uuid.equals(that.uuid) : that.uuid == null;
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}
