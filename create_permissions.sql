USE CRM;
GO

-- Create permissions table
CREATE TABLE permissions (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100),
    resource NVARCHAR(100),
    action NVARCHAR(100),
    description NVARCHAR(255)
);
GO

-- Create role_permission table
CREATE TABLE role_permission (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
);
GO

-- Create staff_role table if it doesn't exist (referenced in PermissionDAO)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='staff_role' AND xtype='U')
BEGIN
    CREATE TABLE staff_role (
        staff_id INT NOT NULL,
        role_id INT NOT NULL,
        PRIMARY KEY (staff_id, role_id),
        FOREIGN KEY (staff_id) REFERENCES users(id),
        FOREIGN KEY (role_id) REFERENCES roles(id)
    );
END
GO
