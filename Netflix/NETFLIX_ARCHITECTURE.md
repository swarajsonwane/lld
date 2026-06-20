# Netflix LLD System Architecture

## Class Diagram

```
                          ┌─────────────────────┐
                          │  NetflixLLDDemo     │
                          │   (Main Entry)      │
                          └──────────┬──────────┘
                                     │
                                     ▼
                          ┌─────────────────────┐
                          │  NetflixSystem      │
                          │  (Singleton)        │
                          │─────────────────────│
                          │ - catalog           │
                          │ - users             │
                          │ - player            │
                          │ - searchService     │
                          │ - contentSubject    │
                          │ - contentFactory    │
                          └──────────┬──────────┘
                                     │
                    ┌────────────────┼────────────────┐
                    │                │                │
                    ▼                ▼                ▼
          ┌──────────────────┐  ┌──────────────┐  ┌──────────────┐
          │ ContentFactory   │  │  VideoPlayer │  │SearchService │
          │ (Factory)        │  │  (State)     │  │              │
          │──────────────────│  │──────────────│  │──────────────│
          │+ createMovie()   │  │- state       │  │+ searchByTitle│
          │+ createSeries()  │  │- status      │  │+ searchByGenre│
          │+ createEpisode() │  │- queue       │  └──────────────┘
          └──────────────────┘  │- currentUser │
                                │+ clickPlay() │
                                │+ clickPause()│
                                │+ clickStop() │
                                │+ clickNext() │
                                └──────┬───────┘
                                       │
                    ┌──────────────────┼──────────────────┐
                    │                  │                  │
                    ▼                  ▼                  ▼
          ┌──────────────────┐ ┌─────────────┐ ┌──────────────┐
          │  StoppedState    │ │PlayingState │ │ PausedState  │
          │(PlayerState)     │ │(PlayerState)│ │(PlayerState) │
          └──────────────────┘ └─────────────┘ └──────────────┘
```

---

## Content Hierarchy (Composite Pattern)

```
                        ┌──────────────┐
                        │   Content    │ (Interface)
                        │  (Component) │
                        └──────┬───────┘
                               │
                ┌──────────────┴──────────────┐
                │                             │
                ▼                             ▼
        ┌──────────────┐           ┌──────────────────┐
        │    Movie     │           │     Series       │
        │   (Leaf)     │           │   (Composite)    │
        │──────────────│           │──────────────────│
        │- id          │           │- id              │
        │- title       │           │- title           │
        │- genre       │           │- genre           │
        │- duration    │           │- episodes[]      │
        │- releaseYear │           │+ addEpisode()    │
        └──────────────┘           │+ getWatchableItems()
                                    └──────┬───────────┘
                                           │
                                           ▼
                                    ┌──────────────┐
                                    │   Episode    │
                                    │   (Leaf)     │
                                    │──────────────│
                                    │- id          │
                                    │- title       │
                                    │- genre       │
                                    │- duration    │
                                    │- episodeNum  │
                                    │- seasonNum   │
                                    └──────────────┘
```

---

## User & Subscription Strategy

```
                        ┌──────────────────┐
                        │      User        │
                        │   (Builder)      │
                        │──────────────────│
                        │- id              │
                        │- name            │
                        │- email           │
                        │- subscription    │
                        │- watchlist[]     │
                        │- watchHistory[]  │
                        │- streamingStrat  │
                        └──────┬───────────┘
                               │
                ┌──────────────┴───────────────┐
                │                              │
    ┌───────────────────────┐   ┌──────────────────────────┐
    │ SubscriptionPlan      │   │  StreamingStrategy       │
    │   (Enum)              │   │    (Strategy)            │
    │───────────────────────│   │──────────────────────────│
    │ BASIC (480p, 1 scr)   │   │ + stream()               │
    │ STANDARD (1080p,2scr) │   │ + getResolution()        │
    │ PREMIUM (4K, 4 scr)   │   └──────────┬───────────────┘
    └───────────────────────┘              │
                                ┌───────────┼───────────┐
                                │           │           │
                                ▼           ▼           ▼
                    ┌──────────────┐ ┌────────────┐ ┌─────────────┐
                    │   Basic      │ │ Standard   │ │  Premium    │
                    │ Strategy     │ │ Strategy   │ │  Strategy   │
                    │ (480p)       │ │ (1080p)    │ │  (4K)       │
                    └──────────────┘ └────────────┘ └─────────────┘
```

---

## Observer Pattern - Content Notifications

```
                    ┌──────────────────────┐
                    │  ContentSubject      │
                    │  (Subject/Publisher) │
                    │──────────────────────│
                    │- observers[]         │
                    │+ subscribe()         │
                    │+ unsubscribe()       │
                    │+ notifyNewContent()  │
                    └──────────┬───────────┘
                               │
                ┌──────────────┴──────────────┐
                │                             │
                ▼                             ▼
        ┌──────────────────┐      ┌──────────────────┐
        │ ContentObserver  │      │      User        │
        │  (Interface)     │      │ (Observer)       │
        │──────────────────│      │──────────────────│
        │+onNewContentAvai│      │ implements       │
        │ lable()          │      │ ContentObserver  │
        └──────────────────┘      │                  │
                                  │ Notified when    │
                                  │ new content      │
                                  │ becomes available│
                                  └──────────────────┘
```

---

## Data Flow Diagram

### 1. Content Addition Flow
```
User Request
     │
     ▼
Netflix.addMovie() ──┐
Netflix.addSeries() ─┼──> ContentFactory.create*()
Netflix.addEpisode()─┤
     │               │
     ├───────────────▼
     │      Create new content with auto ID
     │               │
     ▼               ▼
  catalog.put()  notifyNewContent()
     │               │
     ├───────────────┤
     │               ▼
     │         Notify all observers (Users)
     │               │
     ▼               ▼
  Content stored  Users receive notification
   in system      "New on Netflix: Title"
```

### 2. Content Playback Flow
```
User clicks Play
     │
     ▼
netflix.playContent(content, user)
     │
     ├─► player.load(content, user)
     │        │
     │        ├─► Extract watchable items
     │        ├─► Load user preferences
     │        └─► Set initial state (STOPPED)
     │
     ├─► player.clickPlay()
     │        │
     │        ├─► state.play(player)
     │        │
     │        ├─► VideoPlayer transitions to PlayingState
     │        │
     │        ├─► Retrieves user's StreamingStrategy
     │        │
     │        └─► Calls strategy.stream(content, resumeFrom)
     │
     ▼
Content streams at appropriate quality
(Basic: 480p | Standard: 1080p | Premium: 4K)
     │
     ▼
playCurrentContent() records watch progress
     │
     ▼
User can pause/resume/skip/stop
```

### 3. Search & Browse Flow
```
User Search Request
     │
     ├─► searchByTitle(query)
     │   │
     │   ├─► Filter catalog
     │   ├─► Case-insensitive matching
     │   └─► Return results
     │
     ├─► browseByGenre(genre)
     │   │
     │   ├─► Filter by genre
     │   ├─► Match exact genre
     │   └─► Return all matches
     │
     ▼
Display Results
```

---

## State Machine - Video Player States

```
                    ┌────────────────┐
                    │   STOPPED      │
                    │   (Initial)    │
                    └────────┬───────┘
                             │
                   play()    │
                             ▼
                    ┌────────────────┐
         ┌─────────>│   PLAYING      │
         │          │                │
         │          └─┬──────────────┘
         │            │
         │   pause()  │  stop()
         │            │  │
    resume()         ▼  ▼
         │       ┌────────────┐
         │       │   PAUSED   │
         │       │            │
         │       └─┬──────────┘
         │         │
         │    stop()│ next()
         └─────┐    │
               │    ▼
               │ ┌──────────────┐
               ├>│   STOPPED    │
               │ │              │
               │ └──────────────┘
               │
               └─ play()
```

---

## Technology Stack

| Layer | Technology | Pattern |
|-------|-----------|---------|
| Main | NetflixLLDDemo | Entry Point |
| System | NetflixSystem | Singleton |
| Content | Movie, Series, Episode | Composite |
| Factory | ContentFactory | Factory |
| Player | VideoPlayer | State |
| Streaming | StreamingStrategy | Strategy |
| Observer | ContentSubject, ContentObserver | Observer |
| Search | SearchService | Service Layer |
| Models | User, Content | Data Models |

---

## Execution Flow Summary

```
START
  │
  ├─► Initialize NetflixSystem (Singleton)
  │
  ├─► Create 3 Users (Builder Pattern)
  │   - Register with system (Observer)
  │
  ├─► Add 5 Movies (Factory)
  │   - Notify users
  │
  ├─► Add 2 Series with Episodes (Factory + Composite)
  │   - Notify users
  │
  ├─► Test Search & Browse (Strategy)
  │
  ├─► Manage Watchlist
  │
  ├─► Play Content (State Machine)
  │   - Test all state transitions
  │
  ├─► Track Watch Progress
  │
  ├─► Display Statistics
  │
  └─► END (All tests passed ✓)
```

---

## Key Metrics

- **Total Design Patterns**: 7
- **Total Classes**: 20+
- **Total Interfaces**: 4
- **Total Enums**: 3
- **Test Cases**: 8
- **Data Points**: 50+
- **Lines of Code**: 500+

---

*System is fully functional and ready for extension*

