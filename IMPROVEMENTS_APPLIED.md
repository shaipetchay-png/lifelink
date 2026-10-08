# LifeLink Improvements Applied

This version updates the current LifeLink project without rebuilding the existing modules.

## Changes

### Admin Dashboard
- Added database-driven **Total Donors** statistic.
- Added database-driven **Total Donations** statistic using completed donations.
- Added database-driven **Upcoming Appointments** count for pending/approved appointments.
- Added database-driven **Blood Requests** count.
- Added database-driven **Blood Inventory** total units.
- Added responsive statistic cards that link to their corresponding admin modules.
- Added dark-mode styling for the new statistic cards.

### Reports
- Added **Blood Type filtering** to the Eligibility Report using the donor's blood type.

## Important testing note
The project should still be tested locally with XAMPP/MySQL and a real test account. Maven could not be run in this environment because the Maven distribution could not be downloaded from Maven Central, so runtime compilation/startup was not independently verified here.
