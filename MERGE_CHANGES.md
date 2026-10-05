# Merge of oop_main (pharmacist side) + login_module (admin side)

Base project: oop_main. Nothing was removed from either project's code.

## Copied unchanged (byte-for-byte from login_module)
src/dao/BillingReportsDAO.java, src/gui/AdminHomeFrame.java, src/gui/BillingReportsFrame.java,
src/gui/ManageMedicinesFrame.java, src/gui/ManageUsersFrame.java

## Kept unchanged from oop_main
Every other file, including the restyled LoginFrame layout, PharmacistHomeFrame, all panels,
DatabaseConnection, Session, Main, sql/schema.sql and README.md.

## Not used from login_module
Its Main, Session, DatabaseConnection (points at a different database), PharmacistHomeFrame
(placeholder stub), LoginFrame (replaced by oop_main's restyled one), database.sql.

## Changes to existing files (A = additions only, B = existing lines edited)

### A1. src/model/User.java - lines 56-66 added
Why: admin screens call `new User(id, name, role)` and `getId()`; oop_main's User has neither.
Effect: none on existing code (existing constructor/getters untouched).

### A2. src/model/Medicine.java - lines 91-114 added
Why: admin screens use a String expiry date and getId/getName/getQuantity/getExpiry; oop_main's
Medicine uses java.sql.Date and getMedicineId/getMedicineName/getStockQuantity/getExpiryDate.
Effect: none on existing code. New String constructor converts "yyyy-MM-dd" <-> java.sql.Date.

### A3. src/dao/UserDAO.java - line 10 (import java.util.ArrayList) and lines 61-end added
Why: ManageUsersFrame/BillingReportsFrame need getAllUsers, usernameExists, addUser, deleteUser.
Effect: none on existing code. login_module's login() was NOT copied (validateLogin() does the same job).

### A4. src/dao/MedicineDAO.java - lines 119-end added
Why: ManageMedicinesFrame needs getAll, search, nameExists, add, update, delete.
Effect: none on existing code. Contains edits B1 below.

### B1. src/dao/MedicineDAO.java - lines 132, 179, 193 (inside the code copied in A4)
Why: login_module stores prescription_required as text 'YES'/'NO'; oop_main's schema defines it
as BOOLEAN. Text cannot be saved into a BOOLEAN column, and the text check would always read "no".
  line 132  before: "YES".equals(rs.getString("prescription_required"))));
            after:  rs.getBoolean("prescription_required")));
  line 179  before: ps.setString(6, m.isPrescriptionRequired() ? "YES" : "NO");
            after:  ps.setBoolean(6, m.isPrescriptionRequired());
  line 193  same change as line 179 (update method).
Effect: Manage Medicines add/update/list now read and write the Rx flag correctly. Pharmacist screens unaffected.
(Original locations in login_module/src/dao/MedicineDAO.java: lines 21, 68, 82.)

### B2. src/gui/LoginFrame.java - lines 152-156 (was 152-157)
Why: oop_main's login screen shows "Admin module is not implemented" and stops, so admins could never reach the admin screens.
  before:  if ("ADMIN".equalsIgnoreCase(user.getRole())) {
               JOptionPane.showMessageDialog(this,
                       "Admin module is not implemented in this version.\nPlease log in as a pharmacist.",
                       "Info", JOptionPane.INFORMATION_MESSAGE);
               return;
           }
  after:   if ("ADMIN".equalsIgnoreCase(user.getRole())) {
               new AdminHomeFrame().setVisible(true);
               this.dispose();
               return;
           }
Effect: ADMIN logins open the admin home; PHARMACIST logins behave exactly as before.

## New file: sql/merge_compat.sql  (no Java change)
Why: BillingReportsDAO reads prescriptions.pharmacist_id, which current schema.sql no longer has.
What: adds that column and a trigger that fills it whenever a bill is saved for a prescription.
Run it AFTER sql/schema.sql, every time schema.sql is run (schema.sql drops and recreates the database).
Note: like the original admin design, the reports count prescription-based bills only (walk-in sales have no prescription).

## Setup
1. Run sql/schema.sql, then sql/merge_compat.sql in MySQL.
2. Set your MySQL password in src/database/DatabaseConnection.java.
3. From this folder:  javac -cp "lib/*" -d out $(find src -name "*.java")
                      java -cp "out:lib/*" Main      (use ; instead of : on Windows)
4. Logins: admin1 / admin123 (ADMIN), pharma1 / pharma123 (PHARMACIST).

## Verified / not verified
Verified: compiles with 0 errors; added model code tested; unchanged files confirmed identical by diff.
Not verified: running against MySQL (none available when this was built).

---

# Update: Medicine_module merged into Admin "Manage Medicines"

Clicking **Manage Medicines** on the Admin dashboard now opens the Medicine_module screens
(Dashboard / Add / Manage / Search / View / Stock & Expiry) instead of the old ManageMedicinesFrame.

## New files: src/medicine/  (copied from Medicine_module/oop_project_s3/src)
Medicine_module's files were in the default package, which named packages (gui, dao...) cannot import,
so they were placed in a new package `medicine` (keeps them from clashing with model.Medicine / dao.MedicineDAO).
- Medicine.java      - only added `package medicine;`
- MedicineDAO.java   - added `package medicine;`; `DBConnection` -> `database.DatabaseConnection`
                       (one place to set the DB password); SQL now targets the main project's table:
                       table `medicine` -> `medicines`; columns id -> medicine_id, name -> medicine_name,
                       quantity -> stock_quantity. Admin edits therefore change the SAME stock the pharmacist sells from.
- MedicineGUI.java   - added `package medicine;`; `main(String[])` -> `open()`; added a
                       "Back to Admin Home" button (nav grid 6 -> 7 rows) that returns to AdminHomeFrame.
- Medicine_module's DBConnection.java and database.sql are NOT needed (no separate table/database).

## Changed existing file: src/gui/AdminHomeFrame.java - line 53
  before: new ManageMedicinesFrame().setVisible(true);
  after:  medicine.MedicineGUI.open();

## Notes
- src/gui/ManageMedicinesFrame.java (and the admin methods added to dao/MedicineDAO + model/Medicine) are now
  unused but left in place, nothing was deleted. They can be removed safely.
- No SQL change needed: run sql/schema.sql then sql/merge_compat.sql as before.
- A medicine that has already been sold/prescribed cannot be deleted (foreign keys in bill_items /
  prescription_medicines); the screen shows its normal "could not be deleted" message.
- Medicine ID is typed by the admin on the Add screen (as in Medicine_module); it must be unused.
