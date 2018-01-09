package com.ccz.appinall.library.type.enums;

public enum EHttpStatus {
    eOK,
    eOK_NoContent,
    
    eBadRequest, //400
    eUnauthorized,	//401
    eForbidden,	//403
    eNotFound,	//404
    eMethodNotAllow,	//405
    eNotAcceptable,	//406
    eRequestTimeout, //408
    eLengthRequired,	//411
    eUnsupportedMediaType,	//415
    eRangeNotSatisfiable,	//416
    
    eInternalServerError,	//500
    eNotImplemented,	//501
    eHttpVersionNotSupported,	//505
    
    eInvalidToken,	//498
    eTokenRequired, //499
    eUser_NoBoundary,
    eUser_NoContentDisposition,
    eUser_InvalidServiceAction;

}

