# USSD4ETECSA

 Esta aplicación para la visualizazión de códigos USSD de ETECSA.

![USSD4ETECSA](https://github.com/Daym3l/USSD4ETECSA/blob/master/sample_img/sample.png)

## Funcionalidades

* Recargar saldo.
* Transferir saldo,historial de transferencias.
* Activar planes (Voz,Sms,Datos).
* Visualizar Saldos.
* Notificación actualizada de saldos despues de realizar llamado o enviar sms (experimental).

## Descripción

Para lograr esto, la aplicación captura las respuesta de códigos USSD después ser ejecutados, escucha si un SMS es enviado o recibido y verifica si se han realizado o recepcionado Llamadas, y después de 10 segundos ejecuta automaticamente la verificación de los saldos, validando si se encuentra suscrito a algun servicio de Etecsa.Para ello hace uso de los siguientes servicios y clases (AccessibilityService, Service, ContentObserver, BroadcastReceiver, OrmLiteSqliteOpenHelper).

## Empezando

* Tener androidStudio instalado.

* Importar proyecto.

## Versión
```2.1.0```

## Estado
```Estable```

## Android MinSDK
`4.1.2(16)`

## Autor

* **Daymel Machado Cabrera** - [Daym3l](https://github.com/Daym3l)
