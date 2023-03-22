package com.raf.si.Banka2Backend.models.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document("forexes")
public class Forex {

  @MongoId private String id;
}
