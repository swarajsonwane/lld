# Spotify LLD - Project Structure

## 📁 Directory Layout

```
Spotify/
├── Spotify.iml                          # IntelliJ configuration (Java 21)
├── build.sh                             # Build script
├── run.sh                               # Run script
└── src/
    ├── MusicStreamingDemo.java          # Main demo class
    ├── MusicStreamingSystem.java        # Singleton system
    │
    ├── commands/                        # Command Pattern
    │   ├── Command.java                 # Command interface
    │   ├── PlayCommand.java
    │   ├── PauseCommand.java
    │   ├── NextTrackCommand.java
    │   └── StopCommand.java
    │
    ├── enums/
    │   └── SubscriptionTier.java        # FREE, PREMIUM
    │
    ├── models/
    │   ├── User.java                    # User with Builder
    │   ├── Artist.java
    │   ├── Song.java
    │   ├── Album.java
    │   └── Playlist.java
    │
    ├── observer/
    │   └── (Optional observer functionality)
    │
    ├── recommendations/
    │   └── (Recommendation engine - extensible)
    │
    ├── services/
    │   └── (Catalog and utility services)
    │
    ├── states/                          # State Pattern
    │   ├── Player.java                  # Context
    │   ├── PlayerState.java             # State interface
    │   ├── PlayingState.java
    │   ├── PausedState.java
    │   └── StoppedState.java
    │
    ├── strategies/                      # Strategy Pattern
    │   ├── StreamingStrategy.java       # Strategy interface
    │   ├── FreeUserStrategy.java
    │   └── PremiumUserStrategy.java
    │
    └── Design/
        └── (Design documentation)
```

---

## 📄 File Descriptions

### Main Classes

#### `MusicStreamingDemo.java`
**Purpose**: Entry point and test scenarios  
**Responsibilities**:
- Initialize system
- Create users and playlists
- Execute playback commands
- Display results

#### `MusicStreamingSystem.java` (Singleton)
```
Core system management

Responsibilities:
├─ Manage catalog (artists, songs, albums)
├─ Manage users
├─ Manage playlists
├─ Provide playback services
└─ Handle recommendations

Key Methods:
├─ getInstance()
├─ addArtist(Artist)
├─ addSong(Song)
├─ addAlbum(Album)
├─ createUser()
├─ createPlaylist()
└─ playMusic()
```

---

### Commands (`commands/`)

#### `Command.java` (Interface)
```java
public interface Command {
    void execute();
    void undo();  // Optional undo support
}
```

**Purpose**: Encapsulate playback actions

#### `PlayCommand.java`
```
Encapsulates play action

Responsibilities:
└─ Execute player.play()

Usage:
Command cmd = new PlayCommand(player);
cmd.execute();
```

#### `PauseCommand.java`
```
Encapsulates pause action

Responsibilities:
└─ Execute player.pause()

Usage:
Command cmd = new PauseCommand(player);
cmd.execute();
```

#### `NextTrackCommand.java`
```
Encapsulates skip action

Responsibilities:
├─ Advance to next track
├─ Apply strategy (handle ads for free users)
└─ Execute player.next()

Usage:
Command cmd = new NextTrackCommand(player);
cmd.execute();
```

#### `StopCommand.java`
```
Encapsulates stop action

Responsibilities:
└─ Execute player.stop()

Usage:
Command cmd = new StopCommand(player);
cmd.execute();
```

---

### Enumerations (`enums/`)

#### `SubscriptionTier.java`
```java
public enum SubscriptionTier {
    FREE,      // Ad-supported, limited skips
    PREMIUM    // No ads, unlimited features
}
```

---

### Models (`models/`)

#### `User.java` (with Builder)
```
User representation

Fields:
├─ id: String
├─ name: String
├─ subscription: SubscriptionTier
├─ adSkips: int (only for FREE)
└─ premium: int (for PREMIUM)

Builder Pattern:
new User.Builder("Alice")
    .withSubscription(SubscriptionTier.PREMIUM, 0)
    .build()

Methods:
├─ getId()
├─ getName()
├─ getSubscription()
├─ getRemainingSkips()
└─ canSkip()
```

#### `Artist.java`
```
Artist representation

Fields:
├─ id: String
├─ name: String
└─ bio: String (optional)

Methods:
├─ getId()
├─ getName()
└─ getBio()
```

#### `Song.java`
```
Song/Track representation

Fields:
├─ id: String
├─ title: String
├─ artistId: String
├─ duration: int (seconds)
└─ genre: String

Methods:
├─ getId()
├─ getTitle()
├─ getArtistId()
├─ getDuration()
└─ getGenre()
```

#### `Album.java`
```
Album representation

Fields:
├─ title: String
├─ artist: String
├─ releaseYear: int
└─ tracks: List<Song>

Methods:
├─ addTrack(Song)
├─ getTracks()
├─ getDuration()
└─ getTrackCount()
```

#### `Playlist.java`
```
User-created playlist

Fields:
├─ id: String
├─ name: String
├─ owner: String
├─ songs: List<Song>
├─ followers: Set<String>
└─ createdAt: LocalDateTime

Methods:
├─ addSong(Song)
├─ removeSong(Song)
├─ follow(userId)
├─ getFollowers()
├─ getDuration()
└─ getSongs()
```

---

### States (`states/`)

#### `Player.java` (Context)
```
Player context for State pattern

Responsibilities:
├─ Maintain current state
├─ Delegate to state
├─ Apply streaming strategy
├─ Track current song

Fields:
├─ currentState: PlayerState
├─ currentSong: Song
├─ user: User
└─ strategy: StreamingStrategy

Methods:
├─ setState()
├─ play()
├─ pause()
├─ next()
├─ stop()
└─ getCurrentSong()
```

#### `PlayerState.java` (Interface)
```java
public interface PlayerState {
    void play(Player player);
    void pause(Player player);
    void next(Player player);
    void stop(Player player);
}
```

**Implementations**:
- PlayingState
- PausedState
- StoppedState

#### `PlayingState.java`
```
State: Currently playing music

Behavior:
├─ play() → Already playing
├─ pause() → Transition to PausedState
├─ next() → Skip to next track
└─ stop() → Transition to StoppedState
```

#### `PausedState.java`
```
State: Playback paused

Behavior:
├─ play() → Resume, transition to PlayingState
├─ pause() → Already paused
├─ next() → Not applicable
└─ stop() → Transition to StoppedState
```

#### `StoppedState.java`
```
State: Playback stopped

Behavior:
├─ play() → Start from beginning, transition to PlayingState
├─ pause() → Not applicable
├─ next() → Not applicable
└─ stop() → Already stopped
```

---

### Strategies (`strategies/`)

#### `StreamingStrategy.java` (Interface)
```java
public interface StreamingStrategy {
    void stream(Song song);
    void displayAds();
    String getQuality();
}
```

**Purpose**: Adapt streaming based on subscription

#### `FreeUserStrategy.java`
```
Strategy for FREE tier users

Features:
├─ Standard quality (128 kbps)
├─ Display ads
├─ Limited skips (5 per session)
└─ Track skip usage

Implementation:
├─ stream(): Play with ads
├─ displayAds(): Show advertisement
└─ getQuality(): "128 kbps"

Methods:
└─ canSkip(): Check skip limit
```

#### `PremiumUserStrategy.java`
```
Strategy for PREMIUM tier users

Features:
├─ High quality (320 kbps)
├─ No ads
├─ Unlimited skips
└─ Offline download

Implementation:
├─ stream(): Play without ads
├─ displayAds(): No-op (skip)
└─ getQuality(): "320 kbps"

Methods:
└─ canSkip(): Always true
```

---

## 🔄 Command Execution Flow

```
User Input
    │
    ├─► PlayCommand
    │   └─ new PlayCommand(player).execute()
    │       └─ player.play()
    │           └─ currentState.play(this)
    │               └─ Transition to PlayingState
    │
    ├─► PauseCommand
    │   └─ new PauseCommand(player).execute()
    │       └─ player.pause()
    │           └─ currentState.pause(this)
    │               └─ Transition to PausedState
    │
    ├─► NextTrackCommand
    │   └─ new NextTrackCommand(player).execute()
    │       └─ player.next()
    │           ├─ currentState.next(this)
    │           ├─ Advance song
    │           └─ Apply strategy (ads or not)
    │
    └─► StopCommand
        └─ new StopCommand(player).execute()
            └─ player.stop()
                └─ currentState.stop(this)
                    └─ Transition to StoppedState
```

---

## 📊 State Transitions

```
PlayingState ←→ PausedState
    ↑ ↓           ↓ ↑
    │ └─────────┬─┘ │
    │           ▼   │
    │      StoppedState
    │         │  ▲
    └─────────┴──┘
    
Transitions:
├─ play() in Stopped → PlayingState
├─ pause() in Playing → PausedState
├─ play() in Paused → PlayingState
└─ stop() from any state → StoppedState
```

---

## 💾 Class Relationships

```
MusicStreamingDemo
    │
    ├─ MusicStreamingSystem (Singleton)
    │   ├─ User (with Builder)
    │   ├─ Artist
    │   ├─ Song
    │   ├─ Album
    │   └─ Playlist
    │
    ├─ Player
    │   ├─ PlayerState (Interface)
    │   │   ├─ PlayingState
    │   │   ├─ PausedState
    │   │   └─ StoppedState
    │   └─ StreamingStrategy
    │       ├─ FreeUserStrategy
    │       └─ PremiumUserStrategy
    │
    └─ Commands
        ├─ PlayCommand
        ├─ PauseCommand
        ├─ NextTrackCommand
        └─ StopCommand
```

---

## 🎯 Test Scenarios

### Scenario 1: Premium User Playback
```
1. Create Premium User
   - withSubscription(PREMIUM, 0)

2. Create Playlist
   - Add 4 songs

3. Play Music
   - PlayCommand → Playing state
   - Stream at 320 kbps (high quality)
   - No ads displayed

4. Pause
   - PauseCommand → Paused state

5. Resume
   - PlayCommand → Playing state

6. Skip Track
   - NextTrackCommand → Next song
   - No skip limit

7. Stop
   - StopCommand → Stopped state
```

### Scenario 2: Free User Experience
```
1. Create Free User
   - withSubscription(FREE, 0)

2. Create Playlist
   - Add songs

3. Play Music
   - PlayCommand → Playing state
   - Stream at 128 kbps (standard)
   - Display ads

4. Skip Track
   - NextTrackCommand → Next song
   - Decrement skip count (5 max)
   - Show ads if skips exhausted

5. Cannot skip further
   - Suggest upgrade to Premium
```

---

## 🚀 Build & Run

```bash
# Build
javac -d out/production/Spotify $(find src -name "*.java" -type f)

# Run
java -cp out/production/Spotify MusicStreamingDemo

# Using scripts
bash build.sh
bash run.sh
```

---

## 📋 Usage Examples

### Create User with Builder
```java
User freeUser = new User.Builder("Alice")
    .withSubscription(SubscriptionTier.FREE, 0)
    .build();

User premiumUser = new User.Builder("Bob")
    .withSubscription(SubscriptionTier.PREMIUM, 0)
    .build();
```

### Create Playlist
```java
Playlist playlist = new Playlist("My Playlist", "Alice");
playlist.addSong(song1);
playlist.addSong(song2);
```

### Execute Commands
```java
Command play = new PlayCommand(player);
play.execute();  // Plays music

Command pause = new PauseCommand(player);
pause.execute();  // Pauses music

Command next = new NextTrackCommand(player);
next.execute();  // Skips to next
```

---

## 📈 Statistics

| Metric | Count |
|--------|-------|
| Total Files | 15+ |
| Classes | 12+ |
| Interfaces | 4 |
| Enums | 1 |
| Design Patterns | 5 |
| Commands | 4 |
| States | 3 |
| Strategies | 2 |

---

## 🎓 Patterns Used

| Pattern | Location | Purpose |
|---------|----------|---------|
| Singleton | MusicStreamingSystem | Single system instance |
| Builder | User | Flexible user creation |
| Command | commands/* | Action encapsulation |
| State | states/* | State-based behavior |
| Strategy | strategies/* | Quality adaptation |

---

## 🔗 Extension Points

1. **Add shuffle mode** → New command or state
2. **Add repeat modes** → Player functionality
3. **Add equalizer** → New strategy parameter
4. **Add recommendations** → Recommendation engine
5. **Add offline mode** → Strategy modification
6. **Add social sharing** → New feature
7. **Add podcast support** → Extend models

---

*Spotify Project Structure - Complete Guide*
*Last Updated: June 17, 2026*

