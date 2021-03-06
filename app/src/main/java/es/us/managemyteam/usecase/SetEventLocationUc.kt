package es.us.managemyteam.usecase

import es.us.managemyteam.data.model.LocationBo
import es.us.managemyteam.repository.EventRepository

class SetEventLocationUc(private val eventRepository: EventRepository) {

    suspend operator fun invoke(location: LocationBo?) {
        return eventRepository.setEventLocation(location)
    }

}