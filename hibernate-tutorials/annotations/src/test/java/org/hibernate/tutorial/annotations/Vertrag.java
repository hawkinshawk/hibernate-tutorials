package org.hibernate.tutorial.annotations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "VERTRAG")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vertrag {

    @Id
    @Column(name = "VERTRAG_ID")
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;

    private String vertragsnummer;

    @Version
    private Timestamp version;

    @Singular
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "vertrag")
    private List<Vertragsstatushistorie> vertragsstatushistories;
}
