package com.raf.si.Banka2Backend.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BuySellForexDto {
  /** base currency */
  @NotNull String fromCurrencyCode;
  /** quote currency */
  @NotNull String toCurrencyCode;
  /**
   * Pri kupovini: koliko korisnik zeli da potrosi na kupovinu (a ne koliko zeli da kupi druge
   * valute). Pri prodaji: koliko korisnik zeli da proda od trenutne valute koju ima
   */
  @NotNull Integer amountOfMoney;
}
