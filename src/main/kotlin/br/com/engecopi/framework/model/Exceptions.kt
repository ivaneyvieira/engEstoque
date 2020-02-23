package br.com.engecopi.framework.model

abstract class EModel(msg: String): Exception(msg)

class ECupsPrinter(msg: String): EModel(msg)