package com.daugherty.wrex.exception

class WrexException extends Exception {
  final ERROR_CODE errorCode

  WrexException(ERROR_CODE errorCode) {
    this.errorCode = errorCode
  }
}
