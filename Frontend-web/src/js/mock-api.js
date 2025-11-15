// mock-api.js - Mock API server cho development
const http = require('http');
const url = require('url');
const querystring = require('querystring');

const PORT = 3001;

// Mock data
const mockData = {
    services: [
        {
            id: 1,
            code: 'DV001',
            name: 'Cấp đổi CMND',
            description: 'Dịch vụ cấp đổi chứng minh nhân dân',
            estimatedTime: 15,
            currentQueue: 5,
            active: true,
            createdAt: '2024-01-15T08:00:00Z'
        },
        {
            id: 2,
            code: 'DV002', 
            name: 'Đăng ký hộ khẩu',
            description: 'Dịch vụ đăng ký hộ khẩu thường trú',
            estimatedTime: 20,
            currentQueue: 3,
            active: true,
            createdAt: '2024-01-15T08:00:00Z'
        }
    ],
    queue: [
        {
            id: 1,
            ticketNumber: 'DV001-001',
            serviceCode: 'DV001',
            serviceName: 'Cấp đổi CMND',
            createdAt: new Date(Date.now() - 30 * 60000).toISOString(),
            status: 'waiting'
        },
        {
            id: 2,
            ticketNumber: 'DV002-001',
            serviceCode: 'DV002',
            serviceName: 'Đăng ký hộ khẩu', 
            createdAt: new Date(Date.now() - 15 * 60000).toISOString(),
            status: 'called'
        }
    ],
    feedback: [
        {
            id: 1,
            customerName: 'Nguyễn Văn A',
            contactInfo: 'nguyenvana@email.com',
            serviceCode: 'DV001',
            serviceName: 'Cấp đổi CMND',
            rating: 5,
            comment: 'Dịch vụ rất tốt, nhân viên nhiệt tình',
            status: 'new',
            createdAt: new Date().toISOString()
        }
    ]
};

const server = http.createServer((req, res) => {
    const parsedUrl = url.parse(req.url);
    const path = parsedUrl.pathname;
    const query = querystring.parse(parsedUrl.query);
    
    // CORS headers
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');
    
    if (req.method === 'OPTIONS') {
        res.writeHead(200);
        res.end();
        return;
    }
    
    // API routes
    if (path === '/api/services' && req.method === 'GET') {
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
            services: mockData.services,
            totalPages: 1,
            currentPage: 1
        }));
    }
    else if (path === '/api/queue/current' && req.method === 'GET') {
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify(mockData.queue));
    }
    else if (path === '/api/feedback' && req.method === 'GET') {
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
            feedback: mockData.feedback,
            totalPages: 1,
            currentPage: 1
        }));
    }
    else if (path === '/api/dashboard/statistics' && req.method === 'GET') {
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
            todayServices: 24,
            pendingCases: 8,
            avgRating: 4.5,
            avgWaitTime: 12
        }));
    }
    else {
        res.writeHead(404, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: 'API endpoint not found' }));
    }
});

server.listen(PORT, () => {
    console.log(`Mock API server running on http://localhost:${PORT}`);
    console.log('Available endpoints:');
    console.log('  GET /api/services');
    console.log('  GET /api/queue/current'); 
    console.log('  GET /api/feedback');
    console.log('  GET /api/dashboard/statistics');
});