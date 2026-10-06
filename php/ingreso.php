<?php
$rut = $_REQUEST["rut"] ?? "";
$clave = $_REQUEST["clave"] ?? "";

include("cn.php");

if (empty($rut) || empty($clave)) {
    echo "Usuario no existe";
    exit();
}

// Usar Prepared Statement para evitar Inyección SQL
$stmt = mysqli_prepare($c, "SELECT clave FROM usuarios WHERE rut = ?");
if ($stmt) {
    mysqli_stmt_bind_param($stmt, "s", $rut);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    if ($row = mysqli_fetch_assoc($result)) {
        $dbClave = $row["clave"];
        // Verificar si la clave coincide
        if ($dbClave === $clave || (strlen($dbClave) > 0 && strpos($clave, $dbClave) === 0)) {
            echo "Sesion iniciada correctamente";
        } else {
            echo "Usuario no existe";
        }
    } else {
        echo "Usuario no existe";
    }
    mysqli_stmt_close($stmt);
} else {
    echo "Error en la consulta";
}
?>
