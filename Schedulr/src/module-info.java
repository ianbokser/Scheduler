/**
 * 
 */
module Schedulr {
    requires java.desktop;
    requires java.sql;
    
    // Agregar soporte UTF-8 para todo el módulo
    opens ui;  // Permite reflexión para UI
    
    // Asegurar que los recursos se lean en UTF-8
    // Removed invalid provides directive as UTF8CharsetProvider is not part of the Java standard library.
}