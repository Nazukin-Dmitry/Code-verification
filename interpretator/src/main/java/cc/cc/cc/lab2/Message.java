package cc.cc.cc.lab2;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by 1 on 29.04.2017.
 */
public abstract class Message implements Comparable<Message> {
    public LocalDateTime time = LocalDateTime.now();
    public UUID id = UUID.randomUUID();

    @Override
    public int compareTo(Message o) {
        return time.compareTo(o.time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return id != null ? id.equals(message.id) : message.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
