package ru.web.tsvelenev.WEB.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "showtime")
@Getter
@Setter
@NoArgsConstructor
public class ShowTime implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "showtime_seq")
    @SequenceGenerator(name = "showtime_seq", sequenceName = "showtime_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Column(name = "show_datetime", nullable = false)
    private Date showDatetime;

    @OneToMany(mappedBy = "showTime", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ticket> tickets = new HashSet<>();

    public ShowTime(Performance performance, Date showDatetime) {
        if (performance == null) {
            throw new IllegalArgumentException("Performance cannot be null");
        }
        if (showDatetime == null) {
            throw new IllegalArgumentException("Show datetime cannot be null");
        }
        this.performance = performance;
        this.showDatetime = showDatetime;
    }

    public ShowTime(Performance performance, java.util.Date showDatetime) {
        this(performance, new Date(showDatetime.getTime()));
    }

    public void setDateTime(Date date) {
        this.showDatetime = date;
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setShowTime(this);
    }

    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
        ticket.setShowTime(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShowTime showTime = (ShowTime) o;
        return Objects.equals(id, showTime.id) &&
                Objects.equals(showDatetime, showTime.showDatetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, showDatetime);
    }

    @Override
    public String toString() {
        return "ShowTime{" +
                "id=" + id +
                ", showDatetime=" + showDatetime +
                '}';
    }
}