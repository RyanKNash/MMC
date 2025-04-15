## This is a python port and improve of author John Summers'
## mouse, maze and cheese by RKNash

##for sounds
import simpleaudio as sa
import sys

class Node:
    def __init__(self):
        self.paths = []
        self.blocked = False
        self.origin = False
        self.destination = False

    def put_path(self, c):
        self.paths.append(c)

    def get_path(self):
        if self.paths:
            return self.paths.pop()
        return '*'

    def peek_path(self):
        if self.paths:
            return self.paths[-1]
        return '*'


class Board:
    SIZE = 5

    def __init__(self, board_lines):
        self.cells = [[Node() for _ in range(self.SIZE)] for _ in range(self.SIZE)]
        self.populate_cells(board_lines)
        self.setup_paths()

    def populate_cells(self, lines):
        for i, line in enumerate(lines):
            for j, char in enumerate(line.strip()):
                cell = self.cells[i][j]
                if char == 'S':
                    cell.blocked = False
                    cell.origin = True
                elif char == 'O':
                    cell.blocked = False
                elif char == 'X':
                    cell.blocked = True
                elif char == 'F':
                    cell.blocked = False
                    cell.destination = True

    def setup_paths(self):
        for i in range(self.SIZE):
            for j in range(self.SIZE):
                current = self.cells[i][j]
                if current.blocked:
                    continue
                if i > 0 and not self.cells[i - 1][j].blocked:
                    current.put_path('u')
                if i < self.SIZE - 1 and not self.cells[i + 1][j].blocked:
                    current.put_path('d')
                if j > 0 and not self.cells[i][j - 1].blocked:
                    current.put_path('l')
                if j < self.SIZE - 1 and not self.cells[i][j + 1].blocked:
                    current.put_path('r')

    def get_origin(self):
        for i in range(self.SIZE):
            for j in range(self.SIZE):
                if self.cells[i][j].origin:
                    return (i, j)
        return (0, 0)

    def access_cell(self, x, y):
        return self.cells[x][y]


class MouseBrain:
    def __init__(self, board):
        self.board = board
        self.visited = set()
        self.stack = []
        self.path_memory = {}

        origin = self.board.get_origin()
        self.stack.append(origin)

    def get_next_move(self):
        while self.stack:
            x, y = self.stack.pop()
            if (x, y) in self.visited:
                continue

            self.visited.add((x, y))
            cell = self.board.access_cell(x, y)

            if cell.destination:
                return (x, y, True)

            for dir in reversed(cell.paths):  # reversed for natural DFS behavior
                nx, ny = x, y
                if dir == 'u':
                    nx -= 1
                elif dir == 'd':
                    nx += 1
                elif dir == 'l':
                    ny -= 1
                elif dir == 'r':
                    ny += 1
                if 0 <= nx < self.board.SIZE and 0 <= ny < self.board.SIZE:
                    if (nx, ny) not in self.visited:
                        self.stack.append((nx, ny))

            return (x, y, False)

        return None  # No path found


class StackAITester:
    def __init__(self, board_lines):
        self.space = Board(board_lines)
        self.brain = MouseBrain(self.space)

    def walk_board(self):
        count = 0
        while True:
            result = self.brain.get_next_move()
            if result is None:
                print("*> The mouse goes hungry tonight! <*")
                lose_sound_file = sa.WaveObject.from_wave_file("fail.wav")
                lose_sound = lose_sound_file.play()
                lose_sound.wait_done()
                break

            x, y, found_goal = result
            print(f"**> MOVED TO: ({x+1}, {y+1})")

            if found_goal:
                print(f"*> The Mouse got the cheese at cell ({x+1}, {y+1}) in {count} moves! <*")
                win_sound_file = sa.WaveObject.from_wave_file("complete.wav")
                win_sound = win_sound_file.play()
                win_sound.wait_done()
                break

            count += 1


if __name__ == "__main__":
    import sys

    if len(sys.argv) != 2:
        print("Usage: py MMC.py <board_file>")
    else:
        with open(sys.argv[1], 'r') as f:
            lines = f.readlines()

        tester = StackAITester(lines)
        tester.walk_board()
