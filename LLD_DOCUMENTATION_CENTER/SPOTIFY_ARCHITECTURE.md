# Spotify LLD - Architecture & Design

## System Overview

**Project**: Music Streaming Platform  
**Language**: Java 21  
**Patterns**: 5 (Singleton, Builder, Command, State, Strategy)  
**Components**: 15+ Classes  
**Focus**: Music streaming with playback control and quality management

---

## 🏗️ Architecture Diagram

### High-Level System

```
                    ┌──────────────────────────┐
                    │ MusicStreamingDemo       │
                    │   (Main Entry)           │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                    ┌─────────────────────────────┐
                    │ MusicStreamingSystem         │
                    │ (Singleton)                 │
                    │─────────────────────────────│
                    │ - players                   │
                    │ - catalog                   │
                    │ + addArtist()               │
                    │ + addSong()                 │
                    │ + playMusic()               │
                    └────────────┬────────────────┘
                                 │
                ┌────────────────┼────────────────┐
                │                │                │
                ▼                ▼                ▼
        ┌──────────────┐  ┌──────────┐  ┌──────────────┐
        │User.Builder  │  │Catalog   │  │Player        │
        │(Builder)     │  │(Service) │  │(Component)   │
        └──────────────┘  └──────────┘  └──────┬───────┘
                                                │
                                    ┌───────────┼───────────┐
                                    │           │           │
                                    ▼           ▼           ▼
                            ┌──────────────┐ ┌─────────┐ ┌────────┐
                            │PlayerState   │ │Command  │ │Strategy│
                            │(Interface)   │ │Interface│ │(Stream)│
                            └──────┬───────┘ └────┬────┘ └───┬────┘
                                   │              │         │
                    ┌──────────────┼──────────┐   │         │
                    │              │          │   │         │
                    ▼              ▼          ▼   ▼         ▼
            ┌──────────┐   ┌──────────┐   ┌──────────┐ ┌───────────┐
            │Playing   │   │Paused    │   │Stopped   │ │Streaming  │
            │State     │   │State     │   │State     │ │Strategies │
            └──────────┘   └──────────┘   └──────────┘ └─────┬─────┘
                                                              │
                                        ┌─────────────────────┼─────────────┐
                                        │                     │             │
                                        ▼                     ▼             ▼
                                    ┌────────┐          ┌─────────┐  ┌─────────┐
                                    │PlayCmd │          │Free     │  │Premium  │
                                    │PauseCmd│          │Stream   │  │Stream   │
                                    │NextCmd │          └─────────┘  └─────────┘
                                    │StopCmd │
                                    └────────┘
```

---

## 🔄 Playback Control Flow

```
User Action (Play/Pause/Next/Stop)
    │
    ▼
Command Object Created
    │
    ├─► PlayCommand
    │   └─► Execute
    │       └─► Player.play()
    │
    ├─► PauseCommand
    │   └─► Execute
    │       └─► Player.pause()
    │
    ├─► NextCommand
    │   └─► Execute
    │       └─► Player.next()
    │
    └─► StopCommand
        └─► Execute
            └─► Player.stop()
                    │
                    ▼
            State Transition
                    │
        ┌───────────┼───────────┐
        │           │           │
        ▼           ▼           ▼
    PlayingState PausedState StoppedState
        │
        └─► Streaming Quality Selection
                ├─ Free User: Low quality
                └─ Premium User: High quality
```

---

## 💾 Data Models

### User Model
```
User {
    id: String
    name: String
    subscription: SubscriptionTier (FREE/PREMIUM)
    adSkips: int (only for FREE tier)
    premium: int (premium features available)
    
    Builder Pattern:
    new User.Builder("John")
        .withSubscription(SubscriptionTier.PREMIUM, 0)
        .build()
}
```

### Song Model
```
Song {
    id: String
    title: String
    artistId: String
    duration: int (seconds)
}
```

### Album Model
```
Album {
    title: String
    songs: List<Song>
    
    Methods:
    ├─ addTrack(Song)
    ├─ getTracks()
    └─ getDuration()
}
```

### Playlist Model
```
Playlist {
    id: String
    name: String
    owner: String
    songs: List<Song>
    followers: Set<String>
    
    Methods:
    ├─ addSong(Song)
    ├─ removeSong(Song)
    ├─ follow(userId)
    └─ getFollowers()
}
```

---

## 🎯 Design Patterns Used

### 1. Singleton Pattern
**Where**: MusicStreamingSystem  
**Why**: Single music streaming system instance  
**Implementation**: Thread-safe initialization

```java
public static MusicStreamingSystem getInstance() {
    if (instance == null) {
        synchronized (MusicStreamingSystem.class) {
            if (instance == null) {
                instance = new MusicStreamingSystem();
            }
        }
    }
    return instance;
}
```

### 2. Builder Pattern
**Where**: User class  
**Why**: Flexible user creation with optional fields  
**Features**: Fluent API for configuration

```java
new User.Builder("Alice")
    .withSubscription(SubscriptionTier.PREMIUM, 0)
    .build()
```

### 3. Command Pattern
**Where**: Playback control commands  
**Why**: Encapsulate playback actions as objects  
**Commands**: PlayCommand, PauseCommand, NextCommand, StopCommand

```java
public interface Command {
    void execute();
    void undo();
}
```

### 4. State Pattern
**Where**: Player states  
**Why**: Different player behavior based on state  
**States**: PlayingState, PausedState, StoppedState

```java
public interface PlayerState {
    void play(Player player);
    void pause(Player player);
    void next(Player player);
    void stop(Player player);
}
```

### 5. Strategy Pattern
**Where**: Streaming quality strategies  
**Why**: Different streaming based on subscription  
**Strategies**: FreeUserStrategy, PremiumUserStrategy

```java
public interface StreamingStrategy {
    void stream(Song song);
    void displayAds();
}
```

---

## 🏭 Component Structure

### MusicStreamingSystem (Singleton)
```
Responsibilities:
├─ Manage catalog
├─ Create users
├─ Manage playlists
├─ Coordinate players
└─ Handle recommendations

Methods:
├─ getInstance()
├─ addArtist()
├─ addSong()
├─ addAlbum()
├─ createPlaylist()
└─ playMusic()
```

### Player (Core Component)
```
Responsibilities:
├─ Manage playback state
├─ Execute commands
├─ Apply streaming strategy
└─ Track playing song

Methods:
├─ play()
├─ pause()
├─ next()
├─ stop()
├─ executeCommand(Command)
└─ applyStreamingStrategy()
```

### User (with Builder)
```
Responsibilities:
├─ Define subscription level
├─ Track preferences
├─ Manage playlists
└─ Handle user settings

Builder Features:
├─ Set ID
├─ Set name
├─ Set subscription tier
├─ Set premium features
└─ Build user object
```

### Catalog Service
```
Responsibilities:
├─ Store artists
├─ Store songs
├─ Store albums
└─ Provide search

Methods:
├─ addArtist()
├─ getSong()
├─ getAlbum()
└─ searchSongs()
```

---

## 🔄 Command Pattern Flow

### Execute Commands
```
User Action → Command Created → execute() → State Changed

Examples:
PlayCommand
├─ Create command
├─ Call execute()
└─ Player transitions to PlayingState

PauseCommand
├─ Create command
├─ Call execute()
└─ Player transitions to PausedState

NextCommand
├─ Create command
├─ Call execute()
├─ Advance to next song
└─ Continue playing
```

---

## 📊 Subscription Tiers

### FREE Tier
```
Features:
├─ Standard quality streaming
├─ Ad-supported
├─ Limited skips
└─ No offline download

Streaming Quality:
└─ 128 kbps
```

### PREMIUM Tier
```
Features:
├─ High quality streaming
├─ No ads
├─ Unlimited skips
├─ Offline download
└─ Priority support

Streaming Quality:
└─ 320 kbps
```

---

## 🔗 Pattern Integration

```
MusicStreamingSystem (Singleton)
    ├─ Uses Builder to create Users
    │   └─ User.Builder
    │
    ├─ Commands for playback
    │   ├─ PlayCommand
    │   ├─ PauseCommand
    │   ├─ NextCommand
    │   └─ StopCommand
    │
    ├─ State for player
    │   ├─ PlayingState
    │   ├─ PausedState
    │   └─ StoppedState
    │
    └─ Strategy for streaming
        ├─ FreeUserStrategy
        └─ PremiumUserStrategy
```

---

## 💡 Key Features

✓ **Multi-User Support**
- Different subscription tiers
- User preferences
- Personalized experience

✓ **Playback Control**
- Play, Pause, Skip, Stop
- Command pattern encapsulation
- State-based transitions

✓ **Quality Streaming**
- Adaptive based on subscription
- Ad management for free users
- Premium experience

✓ **Content Management**
- Artists, Songs, Albums
- Playlists with followers
- Recommendations

---

## 📋 Scenario Flows

### Scenario 1: Premium User Playback
```
1. Create Premium User
   - Subscription: PREMIUM
   - Quality: 320 kbps

2. Create Playlist
   - Add songs
   - Set followers

3. Play Music
   - Select song
   - Execute PlayCommand
   - Transition to PlayingState
   - Stream at high quality
   - No ads

4. Skip to Next
   - Execute NextCommand
   - Continue seamlessly

5. Pause Music
   - Execute PauseCommand
   - Transition to PausedState
```

### Scenario 2: Free User Experience
```
1. Create Free User
   - Subscription: FREE
   - Ad skips available

2. Play Music
   - Select song
   - Execute PlayCommand
   - Transition to PlayingState
   - Stream at standard quality

3. Display Ads
   - Show advertisement
   - Decrement skip count

4. Skip Limited
   - After 5 skips
   - Must listen to next song

5. Stop & Resume
   - Can pause/resume
   - Playlist continues
```

---

## 🚀 Execution Flow

```
START
  │
  ├─► Initialize System (Singleton)
  │
  ├─► Setup Catalog
  │   ├─ Add Artists
  │   ├─ Add Songs
  │   └─ Add Albums
  │
  ├─► Create Users
  │   ├─ User 1: FREE (builder pattern)
  │   └─ User 2: PREMIUM (builder pattern)
  │
  ├─► Create Playlists
  │   ├─ Add songs
  │   └─ Add followers
  │
  ├─► Scenario 1: Play Command
  │   ├─ Create PlayCommand
  │   ├─ Execute
  │   ├─ Transition to PlayingState
  │   └─ Start streaming
  │
  ├─► Scenario 2: Pause Command
  │   ├─ Create PauseCommand
  │   ├─ Execute
  │   ├─ Transition to PausedState
  │   └─ Pause playback
  │
  ├─► Scenario 3: Next Command
  │   ├─ Create NextCommand
  │   ├─ Execute
  │   ├─ Skip to next song
  │   └─ Apply strategy (ads or not)
  │
  ├─► Scenario 4: Quality Strategy
  │   ├─ Free user: Standard quality + ads
  │   └─ Premium user: High quality + no ads
  │
  └─► END (Display stats)
```

---

## 📈 Complexity Analysis

| Operation | Complexity | Performance |
|-----------|-----------|-------------|
| Create User | O(1) | Instant |
| Add Song | O(1) | Instant |
| Play Song | O(1) | Instant |
| Execute Command | O(1) | Instant |
| Stream Song | O(1) | Real-time |
| Search Song | O(n) | Depends |

---

## 🎓 Learning Outcomes

From this project, you'll understand:

1. **Singleton Pattern**: System-wide instance management
2. **Builder Pattern**: Complex object construction
3. **Command Pattern**: Action encapsulation
4. **State Pattern**: Behavior management
5. **Strategy Pattern**: Runtime algorithm selection
6. **Real-world Music Streaming**: Features and constraints

---

## 🔍 Extension Points

1. **Add shuffle mode** → New Command or State
2. **Add repeat modes** → Extend Player logic
3. **Add equalizer** → New Strategy pattern
4. **Add recommendations** → New service
5. **Add offline mode** → Extend Strategy
6. **Add social features** → New functionality

---

*Spotify LLD Architecture Document - Complete Reference*
*Last Updated: June 17, 2026*

