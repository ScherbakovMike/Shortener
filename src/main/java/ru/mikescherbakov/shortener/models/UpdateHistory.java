package ru.mikescherbakov.shortener.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "statsUpdateHistory")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateHistory {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
}
