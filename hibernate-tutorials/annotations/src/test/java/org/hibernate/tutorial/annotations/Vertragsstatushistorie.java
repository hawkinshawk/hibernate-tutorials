package org.hibernate.tutorial.annotations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "VERTRAGSSTATUSHISTORIE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "vertrag")
public class Vertragsstatushistorie {

    @Id
    @Column(name = "VERTRAGSSTATUSHISTORIE_ID")
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;

    private String vertragsstatus;

    @Version
    private Timestamp version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VERTRAG_ID", nullable = false)
    private Vertrag vertrag;

}
