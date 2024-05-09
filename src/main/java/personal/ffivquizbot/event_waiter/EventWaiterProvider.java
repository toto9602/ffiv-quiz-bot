package personal.ffivquizbot.event_waiter;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@Getter
public class EventWaiterProvider {
    private final EventWaiter eventWaiter = new EventWaiter();
}
