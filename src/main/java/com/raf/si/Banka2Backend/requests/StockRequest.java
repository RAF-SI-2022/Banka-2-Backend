package com.raf.si.Banka2Backend.requests;

import lombok.Data;

@Data
public class StockRequest {
  String stockSymbol;
  String type; // BUY / SELL
  Integer amount;
  Integer limit;
  Integer stop;
  boolean allOrNone;
  boolean margin;
}
