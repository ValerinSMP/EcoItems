# EcoItems — estado

- Fuente canónica: personal `/root/development/projects/eco-suite/EcoItems`, rama `valerin`. HomeSV referencia intacta. `DEVELOPMENT_HANDOFF.md` ajeno conservado.
- Modificado: atributos de armas calculados desde equipo real; actualizaciones por eventos agrupadas por jugador/tick; retirada y sustitución sin acumulación; limpieza al recargar/deshabilitar. Sin polling ni cálculos nuevos por golpe.
- Compilado: sí, wrapper/toolchain upstream, ejecutor personal limitado. Nueve pruebas JVM correctas; no benchmark ni reproducción de los tickets.
- Instalado: candidato solo Test, SHA256 `e3636d3b6836b3630459dca3aabcf622a68a162c3cade41d7ecbc348aadd9fb1`. Cinco clases sustituidas sobre su runtime original; demás contenido idéntico.
- Respaldo: `/mnt/storage/backups/ai/survival-custom/ecoitems-weapon-attributes-20261011T210540Z`, hash anterior verificado.
- Activo: no activado por el agente. No restart/reload, incluido Test. Custom/Sync intactos, SHA256 `b0d603326e0754abfc6d87ddf7c84f260c6343c56ada46a2676471ff0c9e21b3`.
- Pendiente: propietario activa Test y prueba cambios de armas, inventario, reconexión y recarga. Detalles: `docs/WEAPON-ATTRIBUTES.md`.
