package main.java.com.project.repository;

import com.project.entity.Apartment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApartmentRepository implements PanacheRepository<Apartment> {
    public Apartment findByRoom(String building, String roomNumber) {
        return find("building = ?1 and roomNumber = ?2", building, roomNumber).firstResult();
    }
}
