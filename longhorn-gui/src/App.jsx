// src/App.jsx
import React, { useState, useRef } from 'react';
import networkData from './data.json'; 
import ForceGraph2D from 'react-force-graph-2d'; 
import { Button, Card, CardContent, Typography, Grid, TextField, List, ListItem, ListItemText, Chip } from '@mui/material';
import { createTheme, ThemeProvider } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: '#bf5700', // Burnt Orange
    },
    secondary: {
      main: '#333f48', // Dark Grey
    },
  },
});

function App() {
  const [data, setData] = useState(networkData || { nodes: [], links: [], logs: [] });
  const [searchTerm, setSearchTerm] = useState('');
  const [highlightNodes, setHighlightNodes] = useState(new Set());
  const graphRef = useRef();

  if (!data || !data.nodes) {
      return (
        <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
            <h2>⚠️ Waiting for Data...</h2>
            <p>Please run your Java <code>Main.java</code> to generate the <code>data.json</code> file.</p>
        </div>
      );
  }

  const handleSearch = () => {
    const matches = new Set();
    if (!searchTerm) {
      setHighlightNodes(new Set());
      return;
    }

    data.nodes.forEach(node => {
      if (node.internships && node.internships.some(i => i.toLowerCase().includes(searchTerm.toLowerCase()))) {
        matches.add(node.id);
      }
    });
    setHighlightNodes(matches);
    
    if (matches.size > 0 && graphRef.current) {
        graphRef.current.zoomToFit(400, 50, node => matches.has(node.id));
    }
  };

  return (
    <ThemeProvider theme={theme}>
      <div style={{ padding: '20px', backgroundColor: '#f5f5f5', minHeight: '100vh' }}>
        
        <Typography variant="h3" gutterBottom style={{ color: '#bf5700', fontWeight: 'bold' }}>
          🤘 Longhorn Network Dashboard
        </Typography>

        <Grid container spacing={3}>
          
          {/* LEFT COLUMN: The Graph */}
          <Grid item xs={12} md={8}>
            <Card elevation={3}>
              <CardContent>
                <Typography variant="h5" gutterBottom>Network Visualization</Typography>
                <div style={{ height: '600px', border: '1px solid #ddd', borderRadius: '4px' }}>
                  <ForceGraph2D
                    ref={graphRef}
                    graphData={data}
                    nodeAutoColorBy="group"
                    
                    // --- CUSTOM NODE RENDERING ---
                    nodeCanvasObject={(node, ctx, globalScale) => {
                      const label = node.id;
                      const isHighlighted = highlightNodes.has(node.id);
                      const fontSize = 14 / globalScale; 
                      ctx.font = `bold ${fontSize}px Sans-Serif`;
                      const textWidth = ctx.measureText(label).width;
                      const padding = 10 / globalScale;
                      const radius = Math.max((textWidth / 1.6) + padding, 10 / globalScale);

                      ctx.beginPath();
                      ctx.arc(node.x, node.y, radius, 0, 2 * Math.PI, false);
                      ctx.fillStyle = isHighlighted ? '#ff0000' : (node.color || '#bf5700');
                      ctx.fill();
                      
                      ctx.strokeStyle = '#fff';
                      ctx.lineWidth = 1.5 / globalScale;
                      ctx.stroke();

                      ctx.textAlign = 'center';
                      ctx.textBaseline = 'middle';
                      ctx.fillStyle = 'white'; 
                      ctx.fillText(label, node.x, node.y);
                    }}
                    nodePointerAreaPaint={(node, color, ctx) => {
                        const fontSize = 14; 
                        ctx.font = `bold ${fontSize}px Sans-Serif`;
                        const textWidth = ctx.measureText(node.id).width;
                        const radius = Math.max((textWidth / 1.6) + 10, 10); 
                        ctx.beginPath();
                        ctx.arc(node.x, node.y, radius, 0, 2 * Math.PI, false);
                        ctx.fillStyle = color;
                        ctx.fill();
                    }}

                    // --- CUSTOM LINK RENDERING (Fixed Thickness) ---
                    linkCanvasObject={(link, ctx, globalScale) => {
                        const start = link.source;
                        const end = link.target;
                        if (!start || !end || !start.hasOwnProperty('x') || !end.hasOwnProperty('x')) return;

                        // 1. Draw the Line (CONSTANT THICKNESS)
                        ctx.beginPath();
                        ctx.moveTo(start.x, start.y);
                        ctx.lineTo(end.x, end.y);
                        
                        // CHANGED: Use constant width (1.5) instead of link.value
                        ctx.lineWidth = 1.5 / globalScale; 
                        
                        ctx.strokeStyle = '#999';
                        ctx.stroke();

                        // 2. Draw the Weight Label
                        const textPos = {
                            x: start.x + (end.x - start.x) / 2,
                            y: start.y + (end.y - start.y) / 2
                        };
                        
                        const fontSize = 12 / globalScale;
                        ctx.font = `bold ${fontSize}px Sans-Serif`;
                        const text = String(link.value);

                        const textWidth = ctx.measureText(text).width;
                        ctx.fillStyle = 'rgba(255, 255, 255, 0.9)';
                        ctx.fillRect(
                            textPos.x - textWidth / 2 - 2, 
                            textPos.y - fontSize / 2 - 2, 
                            textWidth + 4, 
                            fontSize + 4
                        );

                        ctx.textAlign = 'center';
                        ctx.textBaseline = 'middle';
                        ctx.fillStyle = '#000';
                        ctx.fillText(text, textPos.x, textPos.y);
                    }}

                    backgroundColor="#ffffff"
                  />
                </div>
                <Typography variant="caption" style={{ marginTop: '10px', display: 'block' }}>
                  * Nodes colored by Major. Numbers represent Connection Strength.
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          {/* RIGHT COLUMN: Controls & Details */}
          <Grid item xs={12} md={4}>
            {/* 1. Referral Path Finder */}
            <Card elevation={3} style={{ marginBottom: '20px' }}>
              <CardContent>
                <Typography variant="h5" gutterBottom>Referral Path Finder</Typography>
                <Typography variant="body2" color="textSecondary" paragraph>
                  Find students who have interned at a specific company.
                </Typography>
                <div style={{ display: 'flex', gap: '10px' }}>
                  <TextField 
                    label="Company (e.g., Google)" 
                    variant="outlined" 
                    size="small" 
                    fullWidth
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                  <Button variant="contained" color="primary" onClick={handleSearch}>
                    Find
                  </Button>
                </div>
                {highlightNodes.size > 0 && (
                  <div style={{ marginTop: '10px' }}>
                    <Typography variant="subtitle2">Found Contacts:</Typography>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '5px', marginTop: '5px' }}>
                      {[...highlightNodes].map(id => (
                        <Chip key={id} label={id} color="success" size="small" />
                      ))}
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>

            {/* 2. Roommate Assignments */}
            <Card elevation={3} style={{ marginBottom: '20px', maxHeight: '300px', overflow: 'auto' }}>
              <CardContent>
                <Typography variant="h5" gutterBottom>Roommate Assignments</Typography>
                <List dense>
                  {data.nodes.filter(n => n.roommate !== "None").map((student, index) => (
                    <ListItem key={index} divider>
                      <ListItemText 
                        primary={
                          <span>
                            <b>{student.id}</b> 🤝 {student.roommate}
                          </span>
                        } 
                      />
                    </ListItem>
                  ))}
                  {data.nodes.every(n => n.roommate === "None") && (
                    <Typography>No roommates assigned yet.</Typography>
                  )}
                </List>
              </CardContent>
            </Card>

            {/* 3. Live Activity Feed */}
            <Card elevation={3} style={{ maxHeight: '300px', overflow: 'auto' }}>
              <CardContent>
                <Typography variant="h5" gutterBottom>Activity Feed</Typography>
                <div style={{ backgroundColor: '#eee', padding: '10px', borderRadius: '4px', fontFamily: 'monospace', fontSize: '12px' }}>
                  {data.logs && data.logs.length > 0 ? (
                    data.logs.map((log, index) => (
                      <div key={index} style={{ marginBottom: '4px', borderBottom: '1px solid #ddd' }}>
                        {log}
                      </div>
                    ))
                  ) : (
                    <Typography variant="caption">No activity recorded.</Typography>
                  )}
                </div>
              </CardContent>
            </Card>

          </Grid>
        </Grid>
      </div>
    </ThemeProvider>
  );
}

export default App;