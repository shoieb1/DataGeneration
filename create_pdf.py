import subprocess
import sys

def install(package):
    subprocess.check_call([sys.executable, "-m", "pip", "install", package])

try:
    from fpdf import FPDF
    from fpdf.enums import XPos, YPos
except ImportError:
    install('fpdf2')
    from fpdf import FPDF
    from fpdf.enums import XPos, YPos

class PDF(FPDF):
    def header(self):
        self.set_font('helvetica', 'B', 15)
        self.cell(0, 10, 'AI Integration in Logistics - Data & Report', new_x=XPos.LMARGIN, new_y=YPos.NEXT, align='C')
        self.ln(5)

    def footer(self):
        self.set_y(-15)
        self.set_font('helvetica', 'I', 8)
        self.cell(0, 10, f'Page {self.page_no()}', new_x=XPos.RIGHT, new_y=YPos.TOP, align='C')

pdf = PDF()
pdf.add_page()
pdf.set_font('helvetica', '', 12)

content = """1. Introduction
Artificial Intelligence (AI) is revolutionizing the logistics industry by optimizing routes, predicting demand, managing inventory, and automating warehouse operations.

2. Sample Data: AI Adoption Impact in Logistics Operations

A. Predictive Analytics for Demand Forecasting
   - Accuracy Improvement: 20-30%
   - Cost Reduction: 10-15%
   - Implementation Rate: 45% of top logistics firms

B. Autonomous Vehicles & Drones
   - Delivery Time Reduction: 25%
   - Fuel Efficiency Gain: 15%
   - Current Testing Phase: Urban and last-mile deliveries

C. Warehouse Automation (Robotics)
   - Order Processing Speed Increase: 40%
   - Error Rate Reduction: 50%
   - ROI Timeframe: 1.5 to 3 years

D. Route Optimization Algorithms
   - Distance Saved per Route: 10-20%
   - Emission Reduction: 15%
   - Real-time Rerouting Capability: Yes

3. Conclusion
Integrating AI in logistics not only drives efficiency but also creates robust, resilient supply chains capable of adapting to global disruptions."""

pdf.multi_cell(0, 8, content)

pdf.output('AI_Integration_in_Logistics.pdf')
print("PDF created successfully: AI_Integration_in_Logistics.pdf")
