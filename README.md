# 🚌 Bus Booking System - Java Swing

## 📌 Project Description
This is a desktop-based Bus Booking System developed using Java Swing.  
It allows users to register, login, book bus tickets, and view booking history.

---

## 🚀 Features

- 👤 User Registration & Login
- 🚌 Bus Ticket Booking
- 📊 Dashboard View
- 📜 Booking History
- 🔐 Session Management
- 🗄️ MySQL Database Integration

---

## 🛠️ Technologies Used

- Java (Swing)
- MySQL
- JDBC
- VS Code / IntelliJ

---

## 📂 Project Structure


BusBookingSystem-Swing/
│
├── db/
│ ├── DBConnection.java
│ ├── query.sql
│
├── ui/
│ ├── LoginPage.java
│ ├── RegisterPage.java
│ ├── Dashboard.java
│ ├── BookingPage.java
│ ├── HistoryPage.java
│ ├── AdminDashboard.java
│
├── UserSession.java
├── manifest.txt



---

## ⚙️ Setup Instructions

### 1. Clone the Repository
git clone https://github.com/your-username/BusBookingSystem-Swing.git

### 2. Open in IDE
Open project in VS Code / IntelliJ

### 3. Setup Database
- Install MySQL
- Run `query.sql` file
- Update DB credentials in:
  `DBConnection.java`

### 4. Run Project
Compile and run:
javac *.java
java LoginPage

---

## 🧠 Future Improvements

- Online payment integration
- Seat selection UI
- Admin analytics dashboard
- Email/SMS notifications

---

## 🤝 Contributing
Pull requests are welcome!

---

## 📄 License
This project is for educational purposes.
