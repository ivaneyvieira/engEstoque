package br.com.engecopi.framework.model

import io.ebean.Model
import io.ebean.annotation.WhenCreated
import io.ebean.annotation.WhenModified
import java.time.LocalDateTime
import javax.persistence.*

@MappedSuperclass abstract class BaseModel(
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0,
  @WhenCreated var createdAt: LocalDateTime = LocalDateTime.now(),
  @WhenModified var updatedAt: LocalDateTime = LocalDateTime.now(),
  @Version var version: Int = 0,
                                          ) : Model()
