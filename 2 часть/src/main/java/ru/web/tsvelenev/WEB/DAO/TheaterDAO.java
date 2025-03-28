package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.Theater;
import java.util.List;

public interface TheaterDAO extends CommonDAO<Theater, Long> {
    List<Theater> getByName(String name);
    Theater getSingleByName(String name);
    List<Theater> getByNameContaining(String namePart);
    List<Theater> getByAddressContaining(String addressPart);
    List<Theater> getByNameAndAddress(String name, String address);
}