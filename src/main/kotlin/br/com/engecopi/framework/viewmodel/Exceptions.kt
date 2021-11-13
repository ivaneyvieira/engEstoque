package br.com.engecopi.framework.viewmodel

abstract class EViewModel(msg: String): Exception(msg)

open class EViewModelError(val msg: String): EViewModel(msg)