package io.parkey19.events;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by parkey19 on 2019. 3. 11..
 */
public interface EventRepository extends JpaRepository<Event, Integer> {
}
