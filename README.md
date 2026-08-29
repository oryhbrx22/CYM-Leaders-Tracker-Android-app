# CYM Leaders Tracker — Firebase + Android

This version keeps the original tracker fields and Save Page as Image function, and adds:
- Firebase Anonymous Authentication for leaders
- Firebase Email/Password Authentication for admin
- Firestore storage for complete monthly submissions
- Admin dashboard with complete per-leader records
- CSV export
- Mobile fit-to-screen layout

## Firebase setup

In Firebase Console for `cym-leaders-web-type-handouts`:

1. Authentication → Sign-in method → enable **Anonymous**.
2. Authentication → Users → create an **Email/Password** admin user with:
   `oryhbrx22@gmail.com`
3. Firestore Database → create the database.
4. Deploy the included `firestore.rules`.

Do not put a Firebase service-account private key in this Android project.

## GitHub Actions

The included workflow builds a debug APK with:
`gradle :app:assembleDebug`

The APK is uploaded as the workflow artifact `cym-tracker-debug-apk`.
