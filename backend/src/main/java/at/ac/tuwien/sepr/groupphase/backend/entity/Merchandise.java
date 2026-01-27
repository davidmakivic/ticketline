package at.ac.tuwien.sepr.groupphase.backend.entity;


import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "merchandise")
public class Merchandise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer price;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private Blob imageData;

    @Column(name = "image_content_type")
    private String imageContentType;

    // Varianten-Liste initialisieren, damit kein NullPointerException auftritt
    @OneToMany(mappedBy = "merchandise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MerchandiseVariant> variants = new ArrayList<>();

    public Merchandise() {
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Blob getImageData() {
        return imageData;
    }

    public void setImageData(Blob imageData) {
        this.imageData = imageData;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public List<MerchandiseVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<MerchandiseVariant> variants) {
        this.variants = variants != null ? variants : new ArrayList<>();
    }

    // Summe der Mengen aller Varianten
    public Integer getQuantity() {
        return variants.stream()
            .map(MerchandiseVariant::getQuantity)
            .reduce(0, Integer::sum);
    }

    public void setQuantity(Integer quantity) {
        if (quantity == null) {
            // nichts tun, vorhandene Varianten behalten ihre Menge
            return;
        }
        if (variants.isEmpty()) {
            MerchandiseVariant defaultVariant = new MerchandiseVariant();
            defaultVariant.setMerchandise(this);
            defaultVariant.setQuantity(quantity);
            this.variants.add(defaultVariant);
        } else {
            variants.forEach(v -> v.setQuantity(quantity));
        }
    }

    // Hilfsmethode, um Variante hinzuzufügen
    public void addVariant(MerchandiseVariant variant) {
        variant.setMerchandise(this);
        this.variants.add(variant);
    }
}
