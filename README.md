# Proyecto Integrador II

El proyecto integrador consiste en un api que maneja las cuentas de Digital Money. El proyecto esta desarrollado en el lenguaje Java, se maneja las dependencias con Maven y su arquitectura es de microservicios.

## Pasos para probar el proyecto

Prerrequisitos:

- SDK Java 11
- Maven 3
- Docker

~~~
git clone  https://gitlab.ctd.academy/ctd/proyecto-integrador-2/proyecto-integrador-1022/0321-ft-c2-back/grupo-02.git
cd grupo-2/Backend
docker compose up
~~~

En cada uno de los microservicios

~~~
mvn clean package
mnv install
mvn spring-boot:run
~~~
## Presentaciones

[Sprint 1](https://www.canva.com/design/DAFP_x49W_g/x2qgGmK23kRmjSCFwiN0pg/view?utm_content=DAFP_x49W_g&utm_campaign=designshare&utm_medium=link&utm_source=publishpresent)

[Sprint 2](https://www.canva.com/design/DAFRfi9R8bM/xW6t_awQk4AWSxofziW4nQ/view?utm_content=DAFRfi9R8bM&utm_campaign=designshare&utm_medium=link&utm_source=publishpresent)

[Sprint 3](https://www.canva.com/design/DAFS3eU6dRI/JJObN4EXPIfBIu2GaKDHLg/view?utm_content=DAFS3eU6dRI&utm_campaign=designshare&utm_medium=link&utm_source=publishpresent)

[Sprint 4](https://www.canva.com/design/DAFUmG1L-9I/cOwRsd6Bp8qbO4u8YGCfHQ/view?utm_content=DAFUmG1L-9I&utm_campaign=designshare&utm_medium=link&utm_source=publishpresent)

## Testing

En este documento se encuentra la suite de pruebas que se compone de testing explotario y casos de prueba. Adicionalmente se encuentra la planilla de defectos.

https://drive.google.com/drive/folders/1gcti0i5V1q312rOE6e0biRBtxyulvmUd

## Infraestructura

Link de deploy: http://ec2-3-139-97-69.us-east-2.compute.amazonaws.com