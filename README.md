# Vidoc

**Vidoc** s a documentation generator for browser workflows that executes your steps in a browser and turns them into visual, shareable reports. Write a `.visc` script, run it, and Vidoc will execute every step in a browser while producing an HTML presentation, a PDF report, and optionally a video recording — all annotated with your comments.

---

## What Problem Does It Solve?

Writing automated browser tests is one thing. Documenting what those tests do, in a way that non-technical stakeholders can read and understand, is another problem entirely. Vidoc solves both at once.

You write one script. Vidoc runs it and generates the documentation for you.

```
# Log into the application
goto "https://myapp.com/login"
type "#username" : "admin"
type "#password" : "secret"
click "#login-btn"

# Verify the dashboard loaded
wait "#dashboard"
assert "#welcome-msg" : "Welcome, admin"
screenshot
```

The comments you write become step descriptions in the generated output. The screenshots are taken automatically or on demand. The result is a fully annotated, visual walkthrough of your script — no extra work required.

---

## Output Formats

Vidoc can generate the following outputs from a single `.visc` script, controlled via CLI flags:

| Flag | Output |
|------|--------|
| `--html` | Interactive HTML presentation with screenshots and step annotations |
| `--pdf` | PDF report with the same content, suitable for sharing |
| `--video` | Screen recording of the full browser session |
| `--all` | All three outputs at once |

```bash
vidoc run login.visc --all
vidoc run checkout.visc --html --pdf
vidoc run search.visc --video
```

---

## The `.visc` File Format

Vidoc scripts use the `.visc` extension (Vidoc Script). The language is intentionally simple and readable — it looks like plain English instructions.

### Comments
Comments start with `#` and must be on their own line. They are not just ignored — they become the step descriptions in your generated documentation.

```
# This comment will appear in the HTML and PDF output
click "#submit-btn"
```

### Variables
Variables are declared with `set` and referenced with a `$` prefix.

```
set $username : "admin"
type "#username" : $username
```

### Full Command Reference

**Navigation**
```
goto "https://myapp.com"
```

**Interaction**
```
click "#selector"
type "#selector" : "value"
type "#selector" : $variable
clear "#selector"
hover "#selector"
select "#dropdown" : "Option"
dragAndDrop "#source" : "#target"
```

**Scrolling**
```
scrollTo "#selector"
scrollDown 500
scrollUp 300
```

**Keyboard**
```
press "Enter"
keyDown "Shift"
keyUp "Shift"
```

**Waiting**
```
wait "#selector"
waitFor 2000
```

**Assertions**
```
assert "#selector" : "expected text"
assertVisible "#selector"
assertHidden "#selector"
assertUrl "https://myapp.com/dashboard"
```

**Documentation**
```
screenshot
screenshot "#selector"
highlight "#selector"
```

---

## Example Script

```
# Go to Wikipedia homepage
goto "https://www.wikipedia.org/"

# Type into the main search input
type "#searchInput": "Selenium (software)"

# Press Enter to submit the search
press "Enter"

# Wait for the page to load
waitFor 3

# Assert that we landed on the correct article
assertUrl "https://en.wikipedia.org/wiki/Selenium_(software)"

# Highlight the article title
highlight "#firstHeading"

# Take a full-page screenshot
screenshot

# Take a screenshot of the infobox (if present)
screenshot ".infobox"
```

---

## Example Output

<div align="center">
  <img src="https://i.imgur.com/yy0uEbs.png" width="900"/>
</div>

---

## Technology Stack

| Technology | Role |
|------------|------|
| **Java 21** | Core language |
| **ANTLR4** | Grammar definition and parser/lexer generation |
| **Selenium 4** | Browser automation and element-level screenshots |
| **Maven** | Build and dependency management |

---

## How It Works

1. You write a `.visc` script with commands and `#` comments
2. The **ANTLR4 lexer** tokenizes the script
3. The **ANTLR4 parser** builds an Abstract Syntax Tree (AST) from the tokens
4. The **execution engine** walks the AST and runs each command via **Selenium**
5. Screenshots are captured automatically or when `screenshot` is called
6. The **documentation generator** combines comments + screenshots into the chosen output format

```
  Your .visc script
      ↓
  ANTLR lexer → tokens
      ↓
  ANTLR parser → AST
      ↓
  Execution engine → Selenium browser actions + screenshots
      ↓
  Documentation generator → HTML / PDF / Video
```

---

## Building the Project

```bash
# Clone the repository
git clone https://github.com/aleksejmutic/vidoc.git
cd vidoc

# Build
./mvnw clean install
```
