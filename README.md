Pedro Caso - 241286
Lab 8 de plataformas
nota: se reutilizó código del laboratorio 6 para realizar este laboratorio 8
En el presente laboratorio se codificó una aplicacion capaz de realizar busquedas de imágenes con scroll infinito y búsqueda en internet, siendo capaz de guardar los datos de búsqueda e imágenes preferidas mediante el caché local y un botón de favoritos. Además la aplicacion le permite al usuario conocer al autor de las imágenes y poder compartirla en caso de que desee. Finalmente la aplicacion cuenta con una tercera pantalla la cual es la pantalla de perfil del usuario, esta pantalla le muestra al usuario su correo asociado y su nombre, además de tener la opción de cambiar el tema de la aplicacion de claro a oscuro. 

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Link del repo:
https://github.com/Pxdro-410/Lab-8-plataformas---Pedro-Caso/tree/Main

Link del video:
https://youtu.be/URPIo_UhmHg

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Preguntas de reflexión:
Consistencia: ¿Cómo invalidarías cache por caducidad (updatedAt) o cambio de parámetros?
Podría eliminar el caché por caducidad simplemente modificando el timestamp, que es un indicador de cuando ocurre cierto evento, como cada registro se guarda con un parámetro de cuando ocurre, simplemente realizaría una comparación del tiempo actual y el del último evento para determinar un límite de cuando se puede considerar caché descartable o no junto con sus asociados.

Offline‑first: ¿Qué conflictos surgen entre cache parcial y paginado? ¿Cómo los resolverías?
Los conflictos que ocurren entre el cache parcial y el paginado es que al usuario solo se le muestra lo que vio o interactuó y por esa razón se muestran páginas con información incompleta en muchos casos, mostrando agujeros en medio de la página donde debería ir una foto en este caso o bien mostrar en distinto orden las fotos en este caso. Esto se puede resolver intentando guardar toda la información cargada o de interes por el usaurio al caché para que, cuando se cargue nuevamente sin conexión, el caché contenga la información más reciente y completa de lo último que el usuario hizo, esto se puede realizar mediante un RemoteMediador que su funcion es limpiar las paginas antiguas y utilizar room para las nuevas.

Escalabilidad: ¿Cuándo conviene introducir ViewModel/Repository y RemoteMediator formal en un proyecto grande?
Segun enteiendo conviene introducir este tipo de funcionalidades cuando se requiere guardar información, específicamente ViewModel cuando se necesite persistencia en la pantalla por ejempo al realizar una accion básica como un scroll o un giro de pantalla, el Repository cuando existen más fuentes de datos para almacenar una lógica de manera más centralizada y finalmente el RemoteMediator en este tipo de casos cuando se maneja información online y offline como el caché y la busqueda de internet, es decir cuando se utiliza room, API y Paging3 ya que todas estas funciones son útiles dependendo del contexto de nuestro proyecto.
