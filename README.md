DESCRIPCION DEL PROYECTO

. Feature-Skeleton: Se realiza una copia de ultimo proyecto en ANDROID realizado en una práctica sobre EH-HO. Como contenido se deja estructura de directorios para poder empezar con una base estructurada. Tambien se conserva las clases relacionadas con la comunicación con la Api. Dentro de las carpetas en 'data' - 'service' se dejan cuatro clases relacionadas con la 
comunicación de la API Discourse.
En el build.gradle (app.) .Se quedan las librerías implementadas por si fueran necesarias.
En el gradle.propierties también se dejan variables prefedefinidas para la comunicación con Discourse. Se dejan los layouts
'view-loading' y 'view-retry' en los xml por si fueran necesarios. Se dejan en la carpeta 'commons' la clase predefinida
ScrollLayoutBehavior para la desaparición del FloatingButton.

. Feature-Login y Alta:
  En primer lugar a pesar de no estar dentro de esta feature se genera la funcionalidad para el visionado de la lista de 
  topics ya que es lo primero que se va a visualizar en la app, se sigue el patrón de arquitectura MVVM para ello. Una
  vez hecho esto se añaden las vista de login y de registro trabajando con constraintlayout. Posteriormante a esto se 
  implementa todo lo que tiene que ver con la funcionalidad del login, registro y restablecimiento de contraseña.
  A continuación se realiza la lógica de la comprobación del inicio de sesion para habilitar permisos de acciones.
  Una vez terminado lo anterior se procede a la implementación del detalle del usuario que en este caso aparece un 
  Alert emergente al pulsar en cada avatar de cada topic.
  
. Feature-search:
  En esta feature se genera toda la lógica para el filtrado de topic en la lista de topics. Para ello se utiliza
  un searchView. Una vez implementado se realizan mejoras para que funcione correctamente también con la persistencia
  en cuanto a cuando no tenemos conexión a internet.
  
  
 . Feature-persistence:
  En esta feature se realiza toda la lógica que corresponde a la persistencia de la app. Para ello se ha utilizado la
  librería room para la base de datos, para ello se genera una base de datos en room en el cual se le carga los 
  datos en cuanto a lista de topics como  lista de posts para posteriormente acceder a dichos datos cuando la app
  lo requiera, que en este caso es para cuando dejamos de tener conexion a internet en el móvil. 
  Ademas se generan permisos para cuando no tienes conexion y estas en modo offline, ademas de por medio de mensajes 
  indicar si se está en modo offline. Las acciones restringidas cuando se está en modo offline son las de creacion
  de topics como la creación de post, con sus respectivos mensajes explicativos.
  
  
 . Feature-improvements:
  En esta feature se ha realizado todas las mejoras posibles que se han quedado pendientes durante el transcurso 
  de las anteriores features, como por ejemplo ciertas validaciones, problemas con sobrecargar de avatares en el 
  visionado de la lista de topics, limpiar y organizar mejor cierto código, etc...
  
