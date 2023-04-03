package com.raf.si.Banka2Backend.models.mariadb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderType; //stock, future, forex...

    private String type; //buy, sell

    private String symbol;

    private int amount;

    private double price;

    private String status;//odobreno, na cekanju, odbijeno

    private boolean finished;

    private String lastModified;

//  todo  private opcija

}
