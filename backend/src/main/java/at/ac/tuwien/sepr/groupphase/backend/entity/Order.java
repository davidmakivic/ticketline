package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private ApplicationUser user;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderMerchItem> merchItems = new LinkedHashSet<>();

    @Column(name = "total_price_cents", nullable = false)
    private long totalPriceCents;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Order() {
    }

    public Order(ApplicationUser user, long totalPriceCents) {
        this.user = user;
        this.totalPriceCents = totalPriceCents;
    }

    public Long getId() {
        return id;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public Set<OrderMerchItem> getMerchItems() {
        return merchItems;
    }

    public long getTotalPriceCents() {
        return totalPriceCents;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void setMerchItems(Set<OrderMerchItem> merchItems) {
        this.merchItems = merchItems != null ? merchItems : new LinkedHashSet<>();
    }

    public void setTotalPriceCents(long totalPriceCents) {
        this.totalPriceCents = totalPriceCents;
    }

    public void addMerchItem(OrderMerchItem item) {
        item.setOrder(this);
        this.merchItems.add(item);
    }
}
