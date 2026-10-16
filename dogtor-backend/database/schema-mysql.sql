
    create table administrador (
        id_administrador integer not null auto_increment,
        user_id_user integer,
        cedula varchar(255) not null,
        correo varchar(255) not null,
        nombre varchar(255) not null,
        nombre_usuario varchar(255) not null,
        foto mediumtext not null,
        primary key (id_administrador)
    ) engine=InnoDB;

    create table cita (
        id_cita integer not null auto_increment,
        id_mascota integer not null,
        id_servicio integer,
        id_veterinario integer,
        fecha_hora datetime(6),
        motivo varchar(1000),
        estado varchar(255) not null,
        sede varchar(255) not null,
        tipo_consulta varchar(255),
        primary key (id_cita)
    ) engine=InnoDB;

    create table cliente (
        id_cliente integer not null auto_increment,
        user_id_user integer,
        cedula varchar(255) not null,
        celular varchar(255) not null,
        correo varchar(255) not null,
        nombre varchar(255) not null,
        nombre_usuario varchar(255) not null,
        foto mediumtext not null,
        primary key (id_cliente)
    ) engine=InnoDB;

    create table evento_clinico (
        fecha date not null,
        fecha_proxima date,
        id_evento integer not null auto_increment,
        id_mascota integer not null,
        id_veterinario integer,
        fecha_registro datetime(6),
        descripcion varchar(2000),
        tipo varchar(255) not null,
        titulo varchar(255) not null,
        primary key (id_evento)
    ) engine=InnoDB;

    create table factura (
        id_cliente integer not null,
        id_factura integer not null auto_increment,
        id_servicio integer,
        id_tratamiento integer,
        pagada BOOLEAN DEFAULT FALSE not null,
        total float(23) not null,
        fecha_hora datetime(6) not null,
        metododepago varchar(255) not null,
        primary key (id_factura)
    ) engine=InnoDB;

    create table factura_medicamento (
        cantidad integer not null,
        id integer not null auto_increment,
        id_factura integer,
        id_medicamento integer,
        primary key (id)
    ) engine=InnoDB;

    create table mascota (
        edad integer,
        estado integer,
        fecha_ingreso date,
        fecha_nacimiento date,
        fecha_salida date,
        id_cliente integer not null,
        id_mascota integer not null auto_increment,
        peso float(23),
        enfermedad varchar(255),
        nombre varchar(255) not null,
        raza varchar(255) not null,
        foto mediumtext not null,
        primary key (id_mascota)
    ) engine=InnoDB;

    create table mascota_adopcion (
        edad_meses integer,
        fecha_publicacion date,
        id_mascota_adopcion integer not null auto_increment,
        id_refugio integer not null,
        estado_salud varchar(1000),
        descripcion varchar(2000),
        ciudad varchar(255) not null,
        especie varchar(255) not null,
        estado varchar(255) not null,
        foto varchar(255) not null,
        nombre varchar(255) not null,
        raza varchar(255),
        sexo varchar(255),
        tamano varchar(255),
        primary key (id_mascota_adopcion)
    ) engine=InnoDB;

    create table medicamento (
        fecha_vencimiento date,
        id_medicamento integer not null auto_increment,
        precio_compra float(23) not null,
        precio_venta float(23) not null,
        stock_minimo integer,
        unidades_disponibles integer not null,
        unidades_vendidas integer not null,
        categoria varchar(255),
        lote varchar(255),
        nombre varchar(255) not null,
        primary key (id_medicamento)
    ) engine=InnoDB;

    create table metodo_pago (
        id_metodo_pago integer not null auto_increment,
        nombre varchar(255) not null,
        primary key (id_metodo_pago)
    ) engine=InnoDB;

    create table publicacion_mascota (
        fecha_suceso date,
        id_publicacion integer not null auto_increment,
        resuelta bit not null,
        fecha_publicacion datetime(6),
        descripcion varchar(2000),
        autor_usuario varchar(255),
        ciudad varchar(255),
        contacto_nombre varchar(255) not null,
        contacto_telefono varchar(255) not null,
        especie varchar(255),
        lugar varchar(255) not null,
        nombre_mascota varchar(255),
        tipo varchar(255) not null,
        foto mediumtext,
        primary key (id_publicacion)
    ) engine=InnoDB;

    create table refugio (
        id_refugio integer not null auto_increment,
        validado bit not null,
        descripcion varchar(1000),
        ciudad varchar(255) not null,
        correo varchar(255),
        logo varchar(255),
        nombre varchar(255) not null,
        telefono varchar(255),
        primary key (id_refugio)
    ) engine=InnoDB;

    create table roles (
        id_rol integer not null auto_increment,
        name varchar(255),
        primary key (id_rol)
    ) engine=InnoDB;

    create table servicio (
        id_servicio integer not null auto_increment,
        precio_base float(23) not null,
        descripcion varchar(255) not null,
        imagen_frontal varchar(255) not null,
        imagen_trasera varchar(255) not null,
        nombre varchar(255) not null,
        primary key (id_servicio)
    ) engine=InnoDB;

    create table servicio_factura (
        id_factura integer not null,
        id_servicio integer not null
    ) engine=InnoDB;

    create table solicitud_adopcion (
        id_mascota_adopcion integer not null,
        id_solicitud integer not null auto_increment,
        fecha datetime(6),
        mensaje varchar(2000),
        ciudad varchar(255),
        correo varchar(255) not null,
        estado varchar(255) not null,
        nombre_solicitante varchar(255) not null,
        telefono varchar(255) not null,
        primary key (id_solicitud)
    ) engine=InnoDB;

    create table testimonio (
        calificacion integer not null,
        fecha date,
        id_cliente integer not null,
        id_servicio integer not null,
        id_testimonio integer not null auto_increment,
        texto varchar(255) not null,
        primary key (id_testimonio)
    ) engine=InnoDB;

    create table tratamiento (
        fecha date not null,
        id_mascota integer,
        id_servicio integer,
        id_tratamiento integer not null auto_increment,
        id_veterinario integer,
        codigo varchar(255) not null,
        detalles varchar(255) not null,
        primary key (id_tratamiento)
    ) engine=InnoDB;

    create table tratamiento_medicamento (
        cantidad integer not null,
        id integer not null auto_increment,
        id_medicamento integer not null,
        id_tratamiento integer not null,
        primary key (id)
    ) engine=InnoDB;

    create table user_roles (
        role_id integer not null,
        user_id integer not null
    ) engine=InnoDB;

    create table users (
        id_user integer not null auto_increment,
        password varchar(255),
        username varchar(255),
        primary key (id_user)
    ) engine=InnoDB;

    create table veterinario (
        estado INTEGER DEFAULT 1 not null,
        id_veterinario integer not null auto_increment,
        numero_atenciones INTEGER DEFAULT 0 not null,
        urgencias24h bit,
        user_id_user integer,
        cedula varchar(255) not null,
        especialidad varchar(255) not null,
        horario varchar(255),
        nombre varchar(255) not null,
        nombre_usuario varchar(255) not null,
        sede varchar(255) not null,
        tarjeta_profesional varchar(255),
        foto mediumtext not null,
        primary key (id_veterinario)
    ) engine=InnoDB;

    alter table administrador 
       add constraint UK_g0rjat4yfyisl087rbh38v0c4 unique (user_id_user);

    alter table administrador 
       add constraint UK_8lm4gy9rulnvw3157f5tmytuv unique (cedula);

    alter table administrador 
       add constraint UK_ko52i5e5dce817rixde2twtdf unique (nombre_usuario);

    alter table cliente 
       add constraint UK_5yc1o0uha63ojpoadqt00kiw5 unique (user_id_user);

    alter table cliente 
       add constraint UK_trfs6xemfuo1u29blh0jw3ekl unique (cedula);

    alter table cliente 
       add constraint UK_gl8ha4e0dyqut6umn1f6diraf unique (nombre_usuario);

    alter table factura 
       add constraint UK_m9lrdtl87xda4s7vpam4t8uay unique (id_tratamiento);

    alter table veterinario 
       add constraint UK_jmfu5703mbkq4k4j6ikuhgsdk unique (user_id_user);

    alter table veterinario 
       add constraint UK_la8p1kxfvitauh3j4py70vahw unique (nombre_usuario);

    alter table administrador 
       add constraint FKb2efbt3hi3ifftbuj0mb9c8hf 
       foreign key (user_id_user) 
       references users (id_user);

    alter table cita 
       add constraint FKqwbuorrv44wpu8s5ny1httcol 
       foreign key (id_mascota) 
       references mascota (id_mascota);

    alter table cita 
       add constraint FK68785k2hlh38mhiq3u2ny82p4 
       foreign key (id_servicio) 
       references servicio (id_servicio);

    alter table cita 
       add constraint FK1judmmkq0bc6khx2ehf69r0f1 
       foreign key (id_veterinario) 
       references veterinario (id_veterinario);

    alter table cliente 
       add constraint FKaa2stjnoqq1jp8hsywwum8bgg 
       foreign key (user_id_user) 
       references users (id_user);

    alter table evento_clinico 
       add constraint FK6n9c1f93lkqvab0t2im9ekfte 
       foreign key (id_mascota) 
       references mascota (id_mascota);

    alter table evento_clinico 
       add constraint FKt9870pciwidmsw7exg8q003k5 
       foreign key (id_veterinario) 
       references veterinario (id_veterinario);

    alter table factura 
       add constraint FKs1vkgwvkxrt5hrmt0c50auyug 
       foreign key (id_cliente) 
       references cliente (id_cliente);

    alter table factura 
       add constraint FKdelmdpgiv2kshi7lkt6yqny7l 
       foreign key (id_servicio) 
       references servicio (id_servicio) 
       on delete set null;

    alter table factura 
       add constraint FKrs9xrwctxy2576xrl8p45xw4a 
       foreign key (id_tratamiento) 
       references tratamiento (id_tratamiento) 
       on delete set null;

    alter table factura_medicamento 
       add constraint FKjg9u7wym9uxl9tv4ptwtr4cxr 
       foreign key (id_factura) 
       references factura (id_factura);

    alter table factura_medicamento 
       add constraint FKlvax0dcwltemcgoncv0ysbwdg 
       foreign key (id_medicamento) 
       references medicamento (id_medicamento);

    alter table mascota 
       add constraint FK9gdh01e7a9o9qry61juxd25uo 
       foreign key (id_cliente) 
       references cliente (id_cliente);

    alter table mascota_adopcion 
       add constraint FK94oh2uibi5i91lsnmur8xpsyj 
       foreign key (id_refugio) 
       references refugio (id_refugio);

    alter table servicio_factura 
       add constraint FKai508tp8s8u03he424wrxb50s 
       foreign key (id_factura) 
       references factura (id_factura);

    alter table servicio_factura 
       add constraint FKeuw9ks0da370j0piubda332nr 
       foreign key (id_servicio) 
       references servicio (id_servicio);

    alter table solicitud_adopcion 
       add constraint FK1vopi9778y41pn1mqp0abdjh 
       foreign key (id_mascota_adopcion) 
       references mascota_adopcion (id_mascota_adopcion);

    alter table testimonio 
       add constraint FKt2dl8bsfaqhqanhnwo9s4iv45 
       foreign key (id_cliente) 
       references cliente (id_cliente);

    alter table testimonio 
       add constraint FKi99t4g1uq5uceuxispuuge5ub 
       foreign key (id_servicio) 
       references servicio (id_servicio);

    alter table tratamiento 
       add constraint FKtpn3dvdx7cqj4pwb3iqygp81l 
       foreign key (id_mascota) 
       references mascota (id_mascota) 
       on delete set null;

    alter table tratamiento 
       add constraint FK3y8c7y0gy8ywjn846rwrqfcn4 
       foreign key (id_servicio) 
       references servicio (id_servicio) 
       on delete set null;

    alter table tratamiento 
       add constraint FK1kl88qq36ntv522m5sgabpmq6 
       foreign key (id_veterinario) 
       references veterinario (id_veterinario) 
       on delete set null;

    alter table tratamiento_medicamento 
       add constraint FKt4i58k26cyydhwv4o7hpskuqj 
       foreign key (id_medicamento) 
       references medicamento (id_medicamento);

    alter table tratamiento_medicamento 
       add constraint FKt1nl44vp0gmlwrok7qj9ivmv4 
       foreign key (id_tratamiento) 
       references tratamiento (id_tratamiento);

    alter table user_roles 
       add constraint FKh8ciramu9cc9q3qcqiv4ue8a6 
       foreign key (role_id) 
       references roles (id_rol);

    alter table user_roles 
       add constraint FKhfh9dx7w3ubf1co1vdev94g3f 
       foreign key (user_id) 
       references users (id_user);

    alter table veterinario 
       add constraint FK2gghaeyu1gtuf7ull4ie732ly 
       foreign key (user_id_user) 
       references users (id_user);
