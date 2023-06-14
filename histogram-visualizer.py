import matplotlib.pyplot as plt

ranges = []
numtuples = []

# Read data from file
with open('input.txt', 'r') as f:
    for line in f:
        line = line.strip().split(', ')
        range_str = line[0].split(': ')[1].strip()
        range_start, range_end = map(float, range_str[1:-1].split(','))
        ranges.append((range_start, range_end))
        numtuples.append(int(line[1].split(': ')[1]))

# Extract range starts and ends into separate lists
range_starts = [r[0] for r in ranges]
range_ends = [r[1] for r in ranges]

# Generate histogram
plt.hist(range_starts, bins=50, weights=numtuples, alpha=0.5, label='Start')
plt.hist(range_ends, bins=50, weights=numtuples, alpha=0.5, label='End')
plt.xlabel('Range')
plt.ylabel('Number of Tuples')
plt.legend()
plt.show()
