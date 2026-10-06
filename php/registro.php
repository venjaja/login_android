<?php
$rut = $_REQUEST["rut"] ?? "";
$clave = $_REQUEST["clave"] ?? "";

include("cn.php");

if (empty($rut) || empty($clave)) {
    echo "Fallo en el registro";
    exit();
}

// 1. Verificar si el usuario ya existe
$stmtCheck = mysqli_prepare($c, "SELECT rut FROM usuarios WHERE rut = ?");
if ($stmtCheck) {
    mysqli_stmt_bind_param($stmtCheck, "s", $rut);
    mysqli_stmt_execute($stmtCheck);
    mysqli_stmt_store_result($stmtCheck);

    if (mysqli_stmt_num_rows($stmtCheck) > 0) {
        echo "El usuario ya esta registrado";
        mysqli_stmt_close($stmtCheck);
        exit();
    }
    mysqli_stmt_close($stmtCheck);
}

// 2. Insertar nuevo usuario mediante
$stmtInsert = mysqli_prepare($c, "INSERT INTO usuarios(rut, clave) VALUES (?, ?)");
if ($stmtInsert) {
    mysqli_stmt_bind_param($stmtInsert, "ss", $rut, $clave);
    if (mysqli_stmt_execute($stmtInsert)) {
        echo "Registro insertado exitoso";
    } else {
        echo "Fallo en el registro";
    }
    mysqli_stmt_close($stmtInsert);
} else {
    echo "Fallo en el registro";
}
?>
