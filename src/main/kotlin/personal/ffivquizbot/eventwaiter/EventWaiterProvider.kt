package personal.ffivquizbot.eventwaiter

import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import org.springframework.stereotype.Service

@Service
class EventWaiterProvider(
) {
    private val _eventWaiter = EventWaiter()

    val eventWaiter: EventWaiter
    get() = _eventWaiter
}




