Progetto TIW,  
Progetto del corso Tecnologia Informatiche per il Web

# University Exam Management System 📚

A complete web application for managing and recording university exam results, developed with MVC architecture and support for asynchronous interactions.

## 🎯 Key Features

### For Teachers:
- **Exam Session Management**: View and select courses with related exam sessions ordered by date
- **Grade Entry**: Single or multiple grade modifications for enrolled students
- **Results Publication**: Publishing grades to make them visible to students
- **Transcript Creation**: Automatic generation of transcripts with all necessary data
- **Dynamic Sorting**: Sortable tables by any field (student ID, surname, grade, etc.)

### For Students:
- **Result Viewing**: Check their own exam results
- **Grade Rejection**: Ability to reject passing grades through intuitive drag & drop
- **Exam History**: Access to all exam sessions they're enrolled in

## 🏗️ Project Architecture

I organized the project following the **Model-View-Controller (MVC)** pattern to ensure clear separation of responsibilities:

### **Model (DAO and Beans)**
```

### **View (Frontend)**
```

### **Controller (Servlets)**
```

## 🛠️ Technologies Used

| Category | Technology | Version | Usage |
|-----------|------------|----------|----------|
| **Backend** | Java | 17+ | Business logic and servlets |
| | JDBC | - | Database connection |
| | Apache Tomcat | 10.0 | Application server |
| **Frontend** | HTML5 | - | Page structure |
| | CSS3 | - | Styling and responsive design |
| | JavaScript (ES6+) | - | Client-side interactions |
| | Thymeleaf | 3.0+ | Server-side template engine |
| **Database** | MySQL | 8.0+ | Data persistence |
| **Build** | Maven | 3.8+ | Dependency management |

## ⚡ Advanced Technical Features

### Single Page Application (SPA)
- **Smooth Navigation**: After login, the entire application works without complete page refreshes
- **Asynchronous Calls**: Use of `fetch()` API for AJAX communications with the server
- **Dynamic Updates**: Real-time DOM modification based on server responses

### Advanced User Interactions
```javascript
// Example of asynchronous call for grade insertion
async function insertMark(studentId, mark) {
    try {
        const response = await fetch('/api/insert-mark', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ studentId, mark })
        });
        const result = await response.json();
        updateStudentRow(studentId, result);
    } catch (error) {
        showErrorMessage('Error inserting grade');
    }
}
```

### Drag & Drop for Grade Rejection
- **Intuitive Interface**: Students can drag the result to a trash can icon
- **Confirmation Popup**: Confirmation modal to prevent accidental actions
- **Visual Feedback**: Animations and hover states to guide the user


## 📝 Usage

### Teacher Access
1. Login with teacher credentials
2. Select course from alphabetically ordered list (descending)
3. Choose exam session from date-ordered list (descending)
4. Manage grades in the enrolled students table:
   - **Single Entry**: Click "EDIT" for each student
   - **Multiple Entry**: Use modal for bulk insertions
   - **Publication**: Click "PUBLISH" to make grades visible
   - **Verbalization**: Click "VERBALIZE" to create final transcript

### Student Access
1. Login with student credentials
2. Select course and exam session
3. View result or "Grade not yet defined" message
4. Optionally reject grade via drag & drop to trash icon

## 🎨 UX/UI Features

- **Responsive Design**: Interface optimized for desktop and mobile
- **Table Sorting**: Click on headers to sort data
- **Visual Feedback**: Loaders, animations, and status messages
- **Accessibility**: Support for screen readers and keyboard navigation

## 🔧 Implemented Customizations

### Grade States
- **Custom Sorting**: `<empty> < absent < deferred < failed < 18 < 19 < ... < 30 < cum laude`
- **Client-side Validation**: Immediate checks on entered grades
- **State Management**: Complete workflow from "not inserted" to "verbalized"

### Security
- **Authentication**: Login system with password hashing
- **Authorization**: Role-based access control (teacher/student)
- **Input Validation**: Sanitization of all input data
- **CSRF Protection**: Security tokens in critical forms

## 📈 Performance and Optimizations

- **Connection Pooling**: Efficient database connection management
- **Caching**: Cache for most frequent queries
- **Lazy Loading**: On-demand data loading
- **Compression**: CSS/JS minification to reduce loading times

---

*Developed as a Web Technologies project - A complete system for digitalizing university management*
